package com.vet.manadawoof.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComprobanteSunatDTO {

    @JsonProperty("operacion")
    private String operacion;

    @JsonProperty("tipo_de_comprobante")
    private Integer tipoDeComprobante;

    @JsonProperty("serie")
    private String serie;

    @JsonProperty("numero")
    private int numero;

    @JsonProperty("sunat_transaction")
    private Integer sunatTransaction;

    @JsonProperty("cliente_tipo_de_documento")
    private Integer clienteTipoDeDocumento;

    @JsonProperty("cliente_numero_de_documento")
    private String clienteNumeroDeDocumento;

    @JsonProperty("cliente_denominacion")
    private String clienteDenominacion;

    @JsonProperty("cliente_direccion")
    private String clienteDireccion;

    @JsonProperty("cliente_email")
    private String clienteEmail;

    @JsonProperty("cliente_email_1")
    private String clienteEmail1;

    @JsonProperty("cliente_email_2")
    private String clienteEmail2;

    @JsonProperty("fecha_de_emision")
    private String fechaDeEmision;

    @JsonProperty("fecha_de_vencimiento")
    private String fechaDeVencimiento;

    @JsonProperty("moneda")
    private Integer moneda;

    @JsonProperty("tipo_de_cambio")
    private String tipoDeCambio;

    @JsonProperty("porcentaje_de_igv")
    private BigDecimal porcentajeDeIgv;

    @JsonProperty("descuento_global")
    private String descuentoGlobal;

    @JsonProperty("total_descuento")
    private String totalDescuento;

    @JsonProperty("total_anticipo")
    private String totalAnticipo;

    @JsonProperty("total_gravada")
    private BigDecimal totalGravada;

    @JsonProperty("total_inafecta")
    private String totalInafecta;

    @JsonProperty("total_exonerada")
    private String totalExonerada;

    @JsonProperty("total_igv")
    private BigDecimal totalIgv;

    @JsonProperty("total_gratuita")
    private String totalGratuita;

    @JsonProperty("total_otros_cargos")
    private String totalOtrosCargos;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("percepcion_tipo")
    private String percepcionTipo;

    @JsonProperty("percepcion_base_imponible")
    private String percepcionBaseImponible;

    @JsonProperty("total_percepcion")
    private String totalPercepcion;

    @JsonProperty("total_incluido_percepcion")
    private String totalIncluidoPercepcion;

    @JsonProperty("retencion_tipo")
    private String retencionTipo;

    @JsonProperty("retencion_base_imponible")
    private String retencionBaseImponible;

    @JsonProperty("total_retencion")
    private String totalRetencion;

    @JsonProperty("total_impuestos_bolsas")
    private String totalImpuestosBolsas;

    @JsonProperty("detraccion")
    private boolean detraccion;

    @JsonProperty("observaciones")
    private String observaciones;

    @JsonProperty("documento_que_se_modifica_tipo")
    private String documentoQueSeModificaTipo;

    @JsonProperty("documento_que_se_modifica_serie")
    private String documentoQueSeModificaSerie;

    @JsonProperty("documento_que_se_modifica_numero")
    private String documentoQueSeModificaNumero;

    @JsonProperty("tipo_de_nota_de_credito")
    private String tipoDeNotaDeCredito;

    @JsonProperty("tipo_de_nota_de_debito")
    private String tipoDeNotaDeDebito;

    @JsonProperty("enviar_automaticamente_a_la_sunat")
    private boolean enviarAutomaticamenteALaSunat;

    @JsonProperty("enviar_automaticamente_al_cliente")
    private boolean enviarAutomaticamenteAlCliente;

    @JsonProperty("condiciones_de_pago")
    private String condicionesDePago;

    @JsonProperty("medio_de_pago")
    private String medioDePago;

    @JsonProperty("placa_vehiculo")
    private String placaVehiculo;

    @JsonProperty("orden_compra_servicio")
    private String ordenCompraServicio;

    @JsonProperty("formato_de_pdf")
    private String formatoDePdf;

    @JsonProperty("generado_por_contingencia")
    private String generadoPorContingencia;

    @JsonProperty("bienes_region_selva")
    private String bienesRegionSelva;

    @JsonProperty("servicios_region_selva")
    private String serviciosRegionSelva;

    @JsonProperty("codigo_unico")
    private String codigoUnico;

    @JsonProperty("nubecont_tipo_de_venta_codigo")
    private String nubecontTipoDeVentaCodigo;

    @JsonProperty("items")
    private List<ItemSunatDTO> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemSunatDTO {

        @JsonProperty("unidad_de_medida")
        private String unidadDeMedida;

        @JsonProperty("codigo")
        private String codigo;

        @JsonProperty("codigo_producto_sunat")
        private String codigoProductoSunat;

        @JsonProperty("descripcion")
        private String descripcion;

        @JsonProperty("cantidad")
        private BigDecimal cantidad;

        @JsonProperty("valor_unitario")
        private BigDecimal valorUnitario;

        @JsonProperty("precio_unitario")
        private BigDecimal precioUnitario;

        @JsonProperty("descuento")
        private String descuento;

        @JsonProperty("subtotal")
        private BigDecimal subtotal;

        @JsonProperty("tipo_de_igv")
        private Integer tipoDeIgv;

        @JsonProperty("igv")
        private BigDecimal igv;

        @JsonProperty("total")
        private BigDecimal total;

        @JsonProperty("anticipo_regularizacion")
        private Boolean anticipoRegularizacion;

        @JsonProperty("anticipo_documento_serie")
        private String anticipoDocumentoSerie;

        @JsonProperty("anticipo_documento_numero")
        private String anticipoDocumentoNumero;
    }
}
