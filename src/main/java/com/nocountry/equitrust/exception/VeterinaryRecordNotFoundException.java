package com.nocountry.equitrust.exception;

/**
 * Se lanza cuando no se encuentra un registro veterinario.
 */
public class VeterinaryRecordNotFoundException extends RuntimeException {

    public VeterinaryRecordNotFoundException(Long recordId) {
        super("Registro veterinario no encontrado con id: " + recordId);
    }
}
