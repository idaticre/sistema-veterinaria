package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.CajaAbrirRequestDTO;
import com.vet.manadawoof.dtos.request.CajaCierreRequestDTO;
import com.vet.manadawoof.dtos.request.CajaMovimientoRequestDTO;
import com.vet.manadawoof.dtos.response.CajaEstadoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaArchivoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaMovimientoResponseDTO;
import com.vet.manadawoof.dtos.response.CajaResumenMedioResponseDTO;
import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.repository.*;
import com.vet.manadawoof.service.CajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private static final BigDecimal CERO = BigDecimal.ZERO.setScale(2);
    private static final ZoneId ZONA_LOCAL = ZoneId.of("America/Lima");

    private final CajaDiariaRepository cajaDiariaRepository;
    private final CajaMovimientoRepository cajaMovimientoRepository;
    private final CajaComprobanteRepository cajaComprobanteRepository;
    private final CajaCierreRepository cajaCierreRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final ClienteRepository clienteRepository;
    private final CajaProductoRepository productoRepository;
    private final ServicioRepository servicioRepository;
    private final AgendaRepository agendaRepository;

    @Override
    @Transactional(readOnly = true)
    public CajaEstadoResponseDTO obtenerEstadoHoy() {
        LocalDate hoy = LocalDate.now(ZONA_LOCAL);
        Optional<CajaDiariaEntity> caja = cajaDiariaRepository.findByFecha(hoy);
        if (caja.isEmpty()) {
            Optional<CajaCierreEntity> cierreAnterior = cajaCierreRepository
                    .findTopByCajaDiaria_FechaLessThanOrderByCajaDiaria_FechaDesc(hoy);
            BigDecimal sugerido = cierreAnterior.map(CajaCierreEntity::getSaldoEfectivoContado).orElse(CERO);
            return CajaEstadoResponseDTO.builder()
                    .fecha(hoy)
                    .abierta(false)
                    .saldoAperturaArrastrado(cierreAnterior.isPresent())
                    .estado("SIN_ABRIR")
                    .saldoAperturaEfectivo(sugerido)
                    .saldoEfectivoEsperado(sugerido)
                    .totalIngresos(CERO)
                    .totalReposiciones(CERO)
                    .totalEgresos(CERO)
                    .resumenPorMedio(List.of())
                    .movimientos(List.of())
                    .build();
        }
        return armarEstado(caja.get());
    }

    @Override
    @Transactional
    public CajaEstadoResponseDTO abrirCaja(CajaAbrirRequestDTO request) {
        LocalDate hoy = LocalDate.now(ZONA_LOCAL);
        Optional<CajaDiariaEntity> existente = cajaDiariaRepository.findByFechaForUpdate(hoy);
        if (existente.isPresent()) {
            if ("CERRADA".equals(existente.get().getEstado())) {
                throw new IllegalArgumentException("La caja de hoy ya está cerrada y no se puede volver a abrir.");
            }
            return armarEstado(existente.get());
        }

        Optional<CajaDiariaEntity> anterior = cajaDiariaRepository.findTopByFechaLessThanOrderByFechaDesc(hoy);
        if (anterior.isPresent() && "ABIERTA".equals(anterior.get().getEstado())) {
            throw new IllegalArgumentException("Primero debe cerrar la caja del " + anterior.get().getFecha() + ".");
        }

        Optional<CajaCierreEntity> cierreAnterior = cajaCierreRepository
                .findTopByCajaDiaria_FechaLessThanOrderByCajaDiaria_FechaDesc(hoy);
        BigDecimal apertura = cierreAnterior
                .map(CajaCierreEntity::getSaldoEfectivoContado)
                .orElse(request.getSaldoAperturaEfectivo());

        if (request.getSaldoAperturaEfectivo() == null || request.getSaldoAperturaEfectivo().signum() < 0) {
            throw new IllegalArgumentException("El saldo de apertura debe ser igual o mayor que cero.");
        }
        if (cierreAnterior.isPresent()
                && apertura.compareTo(request.getSaldoAperturaEfectivo()) != 0) {
            throw new IllegalArgumentException("El saldo inicial viene del efectivo contado al cierre anterior (S/ "
                    + apertura.toPlainString() + "). Para agregar más dinero, registre una reposición después de abrir.");
        }

        CajaDiariaEntity dia = new CajaDiariaEntity();
        dia.setFecha(hoy);
        dia.setSaldoAperturaEfectivo(apertura);
        dia.setEstado("ABIERTA");
        dia.setUsuarioApertura(usuarioActual());
        dia.setFechaApertura(ahora());
        cajaDiariaRepository.save(dia);
        return armarEstado(dia);
    }

    @Override
    @Transactional
    public CajaMovimientoResponseDTO registrarMovimiento(CajaMovimientoRequestDTO request, MultipartFile comprobante) {
        Objects.requireNonNull(request, "Debe enviar los datos del movimiento.");
        String tipo = normalizarTipo(request.getTipo());
        CajaDiariaEntity dia = obtenerCajaAbiertaConBloqueo();
        MedioPagoEntity medio = medioPagoRepository.findById(request.getMedioPagoId())
                .orElseThrow(() -> new IllegalArgumentException("El medio de pago seleccionado no existe."));
        UsuarioEntity usuario = usuarioActual();

        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        if ("REPOSICION".equals(tipo) && !esEfectivo(medio)) {
            throw new IllegalArgumentException("La reposición de la caja debe ingresar en efectivo.");
        }
        validarComprobante(comprobante);

        ClienteEntity cliente = request.getClienteId() == null ? null : clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente seleccionado no existe."));
        ServicioEntity servicio = request.getServicioId() == null ? null : servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new IllegalArgumentException("El servicio seleccionado no existe."));
        AgendaEntity agenda = request.getAgendaId() == null ? null : agendaRepository.findById(request.getAgendaId())
                .orElseThrow(() -> new IllegalArgumentException("La cita seleccionada no existe."));
        if (agenda != null && cliente == null && "INGRESO".equals(tipo)) {
            cliente = agenda.getCliente();
        }

        ProductoEntity producto = null;
        if (request.getProductoId() != null) {
            if (request.getCantidadProducto() == null || request.getCantidadProducto() <= 0) {
                throw new IllegalArgumentException("Indique una cantidad de producto mayor que cero.");
            }
            producto = productoRepository.findByIdForUpdate(request.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("El producto seleccionado no existe."));
        } else if (request.getCantidadProducto() != null) {
            throw new IllegalArgumentException("Seleccione el producto para registrar su cantidad en inventario.");
        }

        if ("EGRESO".equals(tipo)) {
            BigDecimal disponible = calcularSaldoPorMedio(dia, medio);
            if (request.getMonto().compareTo(disponible) > 0) {
                throw new IllegalArgumentException("El gasto supera el saldo disponible para " + medio.getNombre()
                        + ". Disponible: S/ " + disponible.toPlainString() + ".");
            }
        }

        if (producto != null) {
            int stock = producto.getStock() == null ? 0 : producto.getStock();
            int cantidad = request.getCantidadProducto();
            if ("INGRESO".equals(tipo)) {
                if (cantidad > stock) {
                    throw new IllegalArgumentException("No hay suficiente stock. Disponible: " + stock + ".");
                }
                producto.setStock(stock - cantidad);
            } else if ("EGRESO".equals(tipo)) {
                producto.setStock(stock + cantidad);
            } else {
                throw new IllegalArgumentException("Una reposición de caja no puede modificar inventario.");
            }
            productoRepository.save(producto);
        }

        if (agenda != null && "INGRESO".equals(tipo)) {
            actualizarAbonoAgenda(agenda, request.getMonto());
        }

        CajaMovimientoEntity movimiento = new CajaMovimientoEntity();
        movimiento.setCajaDiaria(dia);
        movimiento.setTipo(tipo);
        movimiento.setConcepto(request.getConcepto().trim());
        movimiento.setMonto(request.getMonto().setScale(2, java.math.RoundingMode.HALF_UP));
        movimiento.setMedioPago(medio);
        movimiento.setUsuario(usuario);
        movimiento.setCliente(cliente);
        movimiento.setClienteReferencia(texto(request.getClienteReferencia(), 160));
        movimiento.setProducto(producto);
        movimiento.setCantidadProducto(request.getCantidadProducto());
        movimiento.setServicio(servicio);
        movimiento.setServicioReferencia(texto(request.getServicioReferencia(), 160));
        movimiento.setAgenda(agenda);
        movimiento.setOrigen("MANUAL");
        movimiento.setFechaHora(ahora());
        prepararMetadatosComprobante(movimiento, comprobante);
        CajaMovimientoEntity guardado = cajaMovimientoRepository.save(movimiento);
        guardarArchivoComprobante(guardado, comprobante);
        return mapearMovimiento(guardado);
    }

    @Override
    @Transactional
    public CajaEstadoResponseDTO cerrarCaja(CajaCierreRequestDTO request) {
        CajaDiariaEntity dia = obtenerCajaAbiertaConBloqueo();
        if (request.getSaldoEfectivoContado() == null || request.getSaldoEfectivoContado().signum() < 0) {
            throw new IllegalArgumentException("El efectivo contado debe ser igual o mayor que cero.");
        }
        if (cajaCierreRepository.findByCajaDiaria_Id(dia.getId()).isPresent()) {
            throw new IllegalArgumentException("Esta caja ya tiene un cierre registrado.");
        }

        BigDecimal esperado = calcularSaldoEfectivo(dia);
        BigDecimal contado = request.getSaldoEfectivoContado().setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal diferencia = contado.subtract(esperado).setScale(2, java.math.RoundingMode.HALF_UP);
        String observaciones = texto(request.getObservaciones(), 2000);
        if (diferencia.signum() != 0 && (observaciones == null || observaciones.isBlank())) {
            throw new IllegalArgumentException("Registre el motivo del descuadre antes de cerrar la caja.");
        }

        CajaCierreEntity cierre = new CajaCierreEntity();
        cierre.setCajaDiaria(dia);
        cierre.setSaldoEfectivoEsperado(esperado);
        cierre.setSaldoEfectivoContado(contado);
        cierre.setDiferencia(diferencia);
        cierre.setObservaciones(observaciones);
        cierre.setUsuarioCierre(usuarioActual());
        cierre.setFechaCierre(ahora());
        cajaCierreRepository.save(cierre);

        dia.setEstado("CERRADA");
        cajaDiariaRepository.save(dia);
        return armarEstado(dia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaMovimientoResponseDTO> listarMovimientos(LocalDate desde, LocalDate hasta,
                                                              Integer medioPagoId, String tipo) {
        LocalDate hoy = LocalDate.now(ZONA_LOCAL);
        LocalDate inicio = desde == null ? hoy : desde;
        LocalDate fin = hasta == null ? hoy : hasta;
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final.");
        }
        String tipoNormalizado = tipo == null || tipo.isBlank() ? null : normalizarTipo(tipo);
        return cajaMovimientoRepository.buscarHistorial(inicio, fin, medioPagoId, tipoNormalizado)
                .stream().map(this::mapearMovimiento).toList();
    }

    @Override
    @Transactional
    public void registrarIngresoDesdeComprobante(Long comprobanteId, BigDecimal monto, Integer medioPagoId,
                                                 Long clienteId, Long agendaId, String concepto) {
        if (comprobanteId == null || cajaMovimientoRepository.existsByOrigenAndReferenciaId("COMPROBANTE", comprobanteId)) {
            return;
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto recibido debe ser mayor que cero.");
        }

        CajaDiariaEntity dia = obtenerCajaAbiertaConBloqueo();
        MedioPagoEntity medio = medioPagoRepository.findById(medioPagoId)
                .orElseThrow(() -> new IllegalArgumentException("El medio de pago seleccionado no existe."));
        CajaMovimientoEntity movimiento = new CajaMovimientoEntity();
        movimiento.setCajaDiaria(dia);
        movimiento.setTipo("INGRESO");
        movimiento.setConcepto(texto(concepto, 180));
        movimiento.setMonto(monto.setScale(2, java.math.RoundingMode.HALF_UP));
        movimiento.setMedioPago(medio);
        movimiento.setUsuario(usuarioActual());
        movimiento.setOrigen("COMPROBANTE");
        movimiento.setReferenciaId(comprobanteId);
        movimiento.setFechaHora(ahora());
        if (clienteId != null) {
            ClienteEntity cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("El cliente del comprobante no existe."));
            movimiento.setCliente(cliente);
            movimiento.setClienteReferencia(nombreCliente(cliente));
        }
        if (agendaId != null) {
            AgendaEntity agenda = agendaRepository.findById(agendaId)
                    .orElseThrow(() -> new IllegalArgumentException("La cita del comprobante no existe."));
            movimiento.setAgenda(agenda);
            actualizarAbonoAgenda(agenda, monto);
        }
        cajaMovimientoRepository.save(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaArchivoResponseDTO obtenerArchivoComprobante(Long movimientoId) {
        CajaMovimientoEntity movimiento = cajaMovimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new IllegalArgumentException("El comprobante no existe para este movimiento."));
        CajaComprobanteEntity archivo = cajaComprobanteRepository.findById(movimientoId)
                .orElseThrow(() -> new IllegalArgumentException("El comprobante no existe para este movimiento."));
        return new CajaArchivoResponseDTO(archivo.getArchivo(),
                movimiento.getComprobanteNombre(), movimiento.getComprobanteTipo());
    }

    private CajaEstadoResponseDTO armarEstado(CajaDiariaEntity dia) {
        List<CajaMovimientoEntity> movimientos = cajaMovimientoRepository
                .findByCajaDiaria_IdOrderByFechaHoraDesc(dia.getId());
        BigDecimal ingresos = sumaTipo(movimientos, "INGRESO");
        BigDecimal reposiciones = sumaTipo(movimientos, "REPOSICION");
        BigDecimal egresos = sumaTipo(movimientos, "EGRESO");
        CajaCierreEntity cierre = cajaCierreRepository.findByCajaDiaria_Id(dia.getId()).orElse(null);

        return CajaEstadoResponseDTO.builder()
                .fecha(dia.getFecha())
                .abierta("ABIERTA".equals(dia.getEstado()))
                .saldoAperturaArrastrado(cajaCierreRepository
                        .findTopByCajaDiaria_FechaLessThanOrderByCajaDiaria_FechaDesc(dia.getFecha()).isPresent())
                .estado(dia.getEstado())
                .saldoAperturaEfectivo(dia.getSaldoAperturaEfectivo())
                .saldoEfectivoEsperado(calcularSaldoEfectivo(dia, movimientos))
                .totalIngresos(ingresos)
                .totalReposiciones(reposiciones)
                .totalEgresos(egresos)
                .resumenPorMedio(resumirPorMedio(movimientos))
                .movimientos(movimientos.stream().map(this::mapearMovimiento).toList())
                .saldoEfectivoContado(cierre == null ? null : cierre.getSaldoEfectivoContado())
                .diferencia(cierre == null ? null : cierre.getDiferencia())
                .observacionesCierre(cierre == null ? null : cierre.getObservaciones())
                .usuarioApertura(dia.getUsuarioApertura() == null ? null : dia.getUsuarioApertura().getUsername())
                .usuarioCierre(cierre == null || cierre.getUsuarioCierre() == null ? null : cierre.getUsuarioCierre().getUsername())
                .fechaApertura(dia.getFechaApertura())
                .fechaCierre(cierre == null ? null : cierre.getFechaCierre())
                .build();
    }

    private List<CajaResumenMedioResponseDTO> resumirPorMedio(List<CajaMovimientoEntity> movimientos) {
        Map<Integer, List<CajaMovimientoEntity>> grupos = movimientos.stream()
                .collect(Collectors.groupingBy(m -> m.getMedioPago().getId(), LinkedHashMap::new, Collectors.toList()));
        return grupos.values().stream().map(grupo -> {
            CajaMovimientoEntity primera = grupo.get(0);
            BigDecimal ingresos = grupo.stream().filter(m -> "INGRESO".equals(m.getTipo()))
                    .map(CajaMovimientoEntity::getMonto).reduce(CERO, BigDecimal::add);
            BigDecimal reposiciones = grupo.stream().filter(m -> "REPOSICION".equals(m.getTipo()))
                    .map(CajaMovimientoEntity::getMonto).reduce(CERO, BigDecimal::add);
            BigDecimal egresos = grupo.stream().filter(m -> "EGRESO".equals(m.getTipo()))
                    .map(CajaMovimientoEntity::getMonto).reduce(CERO, BigDecimal::add);
            return CajaResumenMedioResponseDTO.builder()
                    .medioPagoId(primera.getMedioPago().getId())
                    .medioPago(primera.getMedioPago().getNombre())
                    .ingresos(ingresos)
                    .reposiciones(reposiciones)
                    .egresos(egresos)
                    .neto(ingresos.add(reposiciones).subtract(egresos))
                    .build();
        }).toList();
    }

    private CajaMovimientoResponseDTO mapearMovimiento(CajaMovimientoEntity m) {
        String producto = m.getProducto() == null ? null : m.getProducto().getNombre();
        String servicio = m.getServicio() == null
                ? m.getServicioReferencia()
                : m.getServicio().getNombre();
        String cliente = m.getClienteReferencia();
        if ((cliente == null || cliente.isBlank()) && m.getCliente() != null) {
            cliente = nombreCliente(m.getCliente());
        }
        String asociado = producto != null ? producto : servicio;
        return CajaMovimientoResponseDTO.builder()
                .id(m.getId())
                .tipo(m.getTipo())
                .concepto(m.getConcepto())
                .monto(m.getMonto())
                .medioPagoId(m.getMedioPago().getId())
                .medioPago(m.getMedioPago().getNombre())
                .usuario(m.getUsuario().getUsername())
                .cliente(cliente)
                .asociado(asociado)
                .producto(producto)
                .cantidadProducto(m.getCantidadProducto())
                .servicio(servicio)
                .origen(m.getOrigen())
                .fechaHora(m.getFechaHora())
                .comprobanteNombre(m.getComprobanteNombre())
                .tieneComprobante(m.getComprobanteNombre() != null)
                .build();
    }

    private BigDecimal calcularSaldoEfectivo(CajaDiariaEntity dia) {
        return calcularSaldoEfectivo(dia, cajaMovimientoRepository.findByCajaDiaria_IdOrderByFechaHoraDesc(dia.getId()));
    }

    private BigDecimal calcularSaldoEfectivo(CajaDiariaEntity dia, List<CajaMovimientoEntity> movimientos) {
        BigDecimal saldo = dia.getSaldoAperturaEfectivo() == null ? CERO : dia.getSaldoAperturaEfectivo();
        for (CajaMovimientoEntity movimiento : movimientos) {
            if (!esEfectivo(movimiento.getMedioPago())) continue;
            if ("INGRESO".equals(movimiento.getTipo()) || "REPOSICION".equals(movimiento.getTipo())) {
                saldo = saldo.add(movimiento.getMonto());
            } else if ("EGRESO".equals(movimiento.getTipo())) {
                saldo = saldo.subtract(movimiento.getMonto());
            }
        }
        return saldo.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSaldoPorMedio(CajaDiariaEntity dia, MedioPagoEntity medio) {
        if (!esEfectivo(medio)) {
            BigDecimal netoHistorico = cajaMovimientoRepository.calcularNetoHistoricoPorMedio(medio.getId());
            return netoHistorico == null ? CERO : netoHistorico.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        BigDecimal saldo = esEfectivo(medio) ? dia.getSaldoAperturaEfectivo() : CERO;
        for (CajaMovimientoEntity m : cajaMovimientoRepository.findByCajaDiaria_IdOrderByFechaHoraDesc(dia.getId())) {
            if (!m.getMedioPago().getId().equals(medio.getId())) continue;
            if ("INGRESO".equals(m.getTipo()) || "REPOSICION".equals(m.getTipo())) {
                saldo = saldo.add(m.getMonto());
            } else if ("EGRESO".equals(m.getTipo())) {
                saldo = saldo.subtract(m.getMonto());
            }
        }
        return saldo.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal sumaTipo(List<CajaMovimientoEntity> movimientos, String tipo) {
        return movimientos.stream().filter(m -> tipo.equals(m.getTipo()))
                .map(CajaMovimientoEntity::getMonto).reduce(CERO, BigDecimal::add);
    }

    private CajaDiariaEntity obtenerCajaAbiertaConBloqueo() {
        LocalDate hoy = LocalDate.now(ZONA_LOCAL);
        CajaDiariaEntity dia = cajaDiariaRepository.findByFechaForUpdate(hoy)
                .orElseThrow(() -> new IllegalArgumentException("Primero abra la caja del día."));
        if (!"ABIERTA".equals(dia.getEstado())) {
            throw new IllegalArgumentException("La caja del día ya está cerrada. No admite más movimientos.");
        }
        return dia;
    }

    private UsuarioEntity usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new IllegalArgumentException("No se pudo identificar al usuario activo.");
        }
        return usuarioRepository.findByUsernameAndActivoTrue(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("El usuario activo no existe o está inactivo."));
    }

    private void actualizarAbonoAgenda(AgendaEntity agenda, BigDecimal monto) {
        BigDecimal abonado = agenda.getAbonoInicial() == null ? BigDecimal.ZERO : agenda.getAbonoInicial();
        BigDecimal total = agenda.getTotalCita() == null ? BigDecimal.ZERO : agenda.getTotalCita();
        if (total.signum() > 0 && abonado.add(monto).compareTo(total) > 0) {
            throw new IllegalArgumentException("El pago supera el saldo pendiente de la cita.");
        }
        agenda.setAbonoInicial(abonado.add(monto));
        agendaRepository.save(agenda);
    }

    private void validarComprobante(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) return;
        String tipo = archivo.getContentType();
        Set<String> tiposPermitidos = Set.of("application/pdf", "image/jpeg", "image/png", "image/webp");
        if (tipo == null || !tiposPermitidos.contains(tipo.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("El comprobante debe ser un PDF o una imagen.");
        }
        if (archivo.getSize() > 8L * 1024 * 1024) {
            throw new IllegalArgumentException("El comprobante no puede superar 8 MB.");
        }
    }

    private void prepararMetadatosComprobante(CajaMovimientoEntity movimiento, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) return;
        movimiento.setComprobanteNombre(archivo.getOriginalFilename());
        movimiento.setComprobanteTipo(archivo.getContentType());
    }

    private void guardarArchivoComprobante(CajaMovimientoEntity movimiento, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) return;
        try {
            CajaComprobanteEntity comprobante = new CajaComprobanteEntity();
            comprobante.setMovimiento(movimiento);
            comprobante.setArchivo(archivo.getBytes());
            cajaComprobanteRepository.save(comprobante);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo del comprobante.", e);
        }
    }

    private boolean esEfectivo(MedioPagoEntity medio) {
        return medio != null && "EFECTIVO".equalsIgnoreCase(medio.getNombre().trim());
    }

    private String normalizarTipo(String tipo) {
        String valor = tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("INGRESO", "EGRESO", "REPOSICION").contains(valor)) {
            throw new IllegalArgumentException("Tipo de movimiento no válido.");
        }
        return valor;
    }

    private String texto(String valor, int maximo) {
        if (valor == null || valor.isBlank()) return null;
        String limpio = valor.trim();
        return limpio.length() > maximo ? limpio.substring(0, maximo) : limpio;
    }

    private String nombreCliente(ClienteEntity cliente) {
        try {
            return cliente.getEntidad() == null ? null : cliente.getEntidad().getNombre();
        } catch (RuntimeException e) {
            return cliente.getId() == null ? null : "Cliente " + cliente.getId();
        }
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_LOCAL);
    }
}
