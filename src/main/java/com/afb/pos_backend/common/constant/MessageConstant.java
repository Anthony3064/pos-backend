package com.afb.pos_backend.common.constant;

public class MessageConstant {

    // Message for exceptions
    public static final String BAD_REQUEST_ERROR = "El campo %s es obligatorio.";
    public static final String NOT_FOUND_ERROR = "El %s no ha sido encontrado.";
    public static final String DUPLICATE_RESOURCE_ERROR = "El %s ya está en uso.";
    public static final String AUTHENTICATION_ERROR = "Las credenciales de autenticación inválidas.";
    public static final String FORBIDDEN_ERROR = "Acceso denegado. No tienes los permisos necesarios para realizar esta acción";

    // Customs errors
    public static final String BAD_REQUEST_TYPE_ITEM = "El tipo de artículo %s no es permitido.";

    // Message error send email
    public static final String ERROR_MESSAGE_SEND_HTML_EMAIL = "Error al intentar enviar %s a %s";

    public static final String NO_STOCK_ERROR_MESSAGE = "No hay inventario suficiente para el producto \"%s\".";
}
