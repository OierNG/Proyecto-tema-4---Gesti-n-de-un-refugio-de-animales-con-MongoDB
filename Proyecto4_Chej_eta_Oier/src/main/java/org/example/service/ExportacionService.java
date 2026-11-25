package org.example.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.exception.RefugioException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Exporta cualquier lista del modelo a JSON o XML con Jackson
public class ExportacionService {

    private static final Path CARPETA = Path.of("exportaciones");
    private static final DateTimeFormatter MARCA_TIEMPO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ObjectMapper json = configurar(new ObjectMapper());
    private final XmlMapper xml = (XmlMapper) configurar(new XmlMapper());

    // Se serializan los atributos (no los getters) y las fechas como "2025-01-31"
    private static ObjectMapper configurar(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    public Path exportarJSON(List<?> lista, String nombre) {
        Path fichero = rutaFichero(nombre, "json");
        try {
            json.writeValue(fichero.toFile(), lista);
        } catch (IOException e) {
            throw new RefugioException("No se pudo crear el fichero JSON.", e);
        }
        return fichero;
    }

    // Ejemplo: exportarXML(animales, "animales", "animal") -> <animales><animal>...</animal></animales>
    public Path exportarXML(List<?> lista, String nombre, String elemento) {
        Path fichero = rutaFichero(nombre, "xml");
        ObjectNode raiz = xml.createObjectNode();
        raiz.set(elemento, xml.valueToTree(lista));
        try {
            xml.writer().withRootName(nombre).writeValue(fichero.toFile(), raiz);
        } catch (IOException e) {
            throw new RefugioException("No se pudo crear el fichero XML.", e);
        }
        return fichero;
    }

    private Path rutaFichero(String nombre, String extension) {
        try {
            Files.createDirectories(CARPETA);
        } catch (IOException e) {
            throw new RefugioException("No se pudo crear la carpeta de exportaciones.", e);
        }
        return CARPETA.resolve(nombre + "_" + LocalDateTime.now().format(MARCA_TIEMPO) + "." + extension);
    }
}
