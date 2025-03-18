package org.swdc.rmdisk.views.modals;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.File;

public class UpDownloadTask {

    public enum TaskType {

        UPLOAD("upload"), DOWNLOAD("download");

        private String icon;

        TaskType(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }

    private TaskType taskType;

    private File file;

    private long length;

    private SimpleBooleanProperty cancelled = new SimpleBooleanProperty(false);

    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0);

    private SimpleBooleanProperty finished = new SimpleBooleanProperty(false);

    private double totalBytes;

    public UpDownloadTask(TaskType taskType, File file, long length) {
        this.taskType = taskType;
        this.file = file;
        this.length = length;
    }

    public void updateProgress(double progress) {
        totalBytes += progress;
        this.progress.set(totalBytes / length);
        if (totalBytes >= length) {
            finished.set(true);
        }
    }

    public void cancel() {
        cancelled.set(true);
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public boolean isFinished() {
        return finished.get();
    }

    public SimpleBooleanProperty finishedProperty() {
        return finished;
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    public File getFile() {
        return file;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
