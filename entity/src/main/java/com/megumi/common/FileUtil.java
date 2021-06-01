package com.megumi.common;

import com.megumi.common.IdGenerator;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtil {

    private final IdGenerator idGenerator;
    private final Path savePath;

    public FileUtil(IdGenerator idGenerator, String savePath) throws IOException {
        this.idGenerator = idGenerator;
        this.savePath = Paths.get(savePath);
        if (Files.notExists(this.savePath)) {
            Files.createDirectories(this.savePath);
        }
    }

    public String save(MultipartFile file, String path) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (path.isBlank()) {
            return save(file);
        }
        Path filePath = Paths.get(path, createFileName(file));
        if (Files.notExists(Path.of(path))) {
            Files.createDirectories(Path.of(path));
        }
        if (Files.exists(filePath)) {
            throw new FileAlreadyExistsException("该文件已存在");
        } else {
            Files.createFile(filePath);
        }
        file.transferTo(filePath);
        return filePath.toString();
    }

    public String getName(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public String save(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Path filePath = Paths.get(savePath.toString(), createFileName(file));
        if (Files.exists(filePath)) {
            throw new FileAlreadyExistsException("该文件已存在");
        } else {
            Files.createFile(filePath);
        }
        file.transferTo(filePath);
        return filePath.toString();
    }

    private String createFileName(MultipartFile file) {
        var originalFilename = file.getOriginalFilename();
        var suffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        return idGenerator.getNextId() + suffix;
    }

    public File createFile(String suffix, String savePath) throws IOException {
        return Files.createFile(Path.of(savePath, idGenerator.getNextId() + "." + suffix)).toFile();
    }
}
