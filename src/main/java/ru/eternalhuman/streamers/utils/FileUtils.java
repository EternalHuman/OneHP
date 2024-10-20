package ru.eternalhuman.streamers.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

@UtilityClass
public class FileUtils {
    public static void cleanDirectory(File directory) throws IOException {
        File[] files = verifiedListFiles(directory);
        IOException exception = null;
        int len$ = files.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            File file = files[i$];

            try {
                forceDelete(file);
            } catch (IOException var8) {
                exception = var8;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public static File[] verifiedListFiles(File directory) throws IOException {
        String message;
        if (!directory.exists()) {
            message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        } else if (!directory.isDirectory()) {
            message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        } else {
            File[] files = directory.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                return files;
            }
        }
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }

    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (!isSymlink(directory)) {
                cleanDirectory(directory);
            }

            if (!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        } else {
            File fileInCanonicalDir;
            if (file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                File canonicalDir = file.getParentFile().getCanonicalFile();
                fileInCanonicalDir = new File(canonicalDir, file.getName());
            }

            return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile()) || isBrokenSymlink(file);
        }
    }

    private static boolean isBrokenSymlink(File file) throws IOException {
        if (file.exists()) {
            return false;
        } else {
            final File canon = file.getCanonicalFile();
            File parentDir = canon.getParentFile();
            if (parentDir != null && parentDir.exists()) {
                File[] fileInDir = parentDir.listFiles(new FileFilter() {
                    public boolean accept(File aFile) {
                        return aFile.equals(canon);
                    }
                });
                return fileInDir != null && fileInDir.length > 0;
            } else {
                return false;
            }
        }
    }
}
