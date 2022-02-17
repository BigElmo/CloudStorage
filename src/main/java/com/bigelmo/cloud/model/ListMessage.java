package com.bigelmo.cloud.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ListMessage implements ExchangeMessage {

    private final boolean isRootDir;
    private final boolean isHasParent;
    private final Path path;
    private final List<String> fileNames;

    public ListMessage(Path path, boolean isRootDir) throws IOException {
        this.path = path;
        isHasParent = (path.getParent() != null);
        this.isRootDir = isRootDir;
        fileNames = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST;
    }
}
