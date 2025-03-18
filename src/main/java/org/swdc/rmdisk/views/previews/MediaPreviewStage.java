package org.swdc.rmdisk.views.previews;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;

/**
 * 音视频文件的预览窗口。
 */
public class MediaPreviewStage extends Stage {

    private MediaPlayer player;

    private Slider progressBar;

    private Slider volumeBar;

    private Button playButton;

    private Button volumeButton;

    private Label timeLabel;

    private BorderPane root;

    private HBox controls;

    private MediaView mediaView;

    private Fontawsome5Service fontawsome5Service;

    private String formatSecond = "%02d:%02d";

    public MediaPreviewStage(String url, Fontawsome5Service fontawsome5Service) {

        this.fontawsome5Service = fontawsome5Service;

        this.root = new BorderPane();

        playButton = new Button("Play");
        playButton.setFont(fontawsome5Service.getSolidFont(FontSize.MIDDLE));
        playButton.setText(fontawsome5Service.getFontIcon("play"));
        playButton.setPadding(new Insets(8));
        playButton.setDisable(true);
        playButton.setOnAction(this::onPlayOrPause);
        playButton.getStyleClass().add("tool-button");

        volumeButton = new Button();
        volumeButton.getStyleClass().add("tool-button");
        volumeButton.setFont(fontawsome5Service.getSolidFont(FontSize.SMALL));
        volumeButton.setText(fontawsome5Service.getFontIcon("volume-up"));
        volumeButton.setPadding(new Insets(8));
        volumeButton.setPrefSize(36,36);
        volumeButton.setOnAction(this::onMute);

        volumeBar = new Slider();
        volumeBar.setMin(0);
        volumeBar.setMax(100);
        volumeBar.getStyleClass().add("slider-bar");
        volumeBar.setPrefWidth(80);
        volumeBar.setValue(50);
        volumeBar.valueProperty().addListener(this::onVolumeChanged);

        progressBar = new Slider();
        progressBar.getStyleClass().add("slider-bar");
        progressBar.prefWidthProperty().bind(this.widthProperty().subtract(280));
        progressBar.setOnMousePressed(this::onProgressPressed);
        progressBar.setOnMouseReleased(this::onProgressReleased);

        timeLabel = new Label("00:00");

        controls = new HBox();
        controls.setAlignment(Pos.CENTER);
        controls.setSpacing(12);
        controls.setPadding(new Insets(8));
        controls.getChildren().addAll(playButton,progressBar, timeLabel, volumeButton, volumeBar);

        root.setBottom(controls);
        root.getStyleClass().add("stage-dialog");

        setScene(new Scene(root));

        this.player = new MediaPlayer(new Media(url));
        this.player.setOnReady(this::onMediaReady);
        this.player.currentTimeProperty().addListener(this::onMediaPlaying);
        this.player.setOnError(this::onMediaError);
        setOnHidden(this::onStageHidden);
    }

    /**
     * 媒体加载失败，关闭窗口。
     */
    private void onMediaError() {
        if (this.player.getError() != null) {
            this.close();
        }
    }

    /**
     * 窗口关闭事件，释放资源。
     * @param windowEvent
     */
    private void onStageHidden(WindowEvent windowEvent) {
        if (player == null) {
            return;
        }
        player.stop();
        player.dispose();
        player = null;
    }

    /**
     * 播放或暂停。
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void onMediaPlaying(Observable observable, Duration oldValue, Duration newValue) {
        int minutes = Double.valueOf(newValue.toMinutes()).intValue();
        int seconds = Double.valueOf(newValue.toSeconds() - minutes * 60).intValue();
        timeLabel.setText(String.format(formatSecond, minutes, seconds));
        progressBar.setValue(newValue.toSeconds());
    }

    /**
     * 进度条按下事件。
     * @param event
     */
    private void onProgressPressed(MouseEvent event) {
        if (player == null) {
            return;
        }
        player.pause();
        playButton.setText(fontawsome5Service.getFontIcon("play"));
    }

    /**
     * 释放进度条事件。
     * @param event
     */
    private void onProgressReleased(MouseEvent event) {
        if (player == null) {
            return;
        }
        player.seek(Duration.seconds(progressBar.getValue()));
        player.play();
        playButton.setText(fontawsome5Service.getFontIcon("pause"));
    }

    /**
     * 静音或取消静音。
     * @param event
     */
    private void onMute(ActionEvent event) {
        if (player == null) {
            return;
        }
        if(player.isMute()) {
            volumeButton.setText(fontawsome5Service.getFontIcon("volume-up"));
            player.setMute(false);
        } else {
            volumeButton.setText(fontawsome5Service.getFontIcon("volume-mute"));
            player.setMute(true);
        }
    }

    /**
     * 音量变化事件。
     * @param observable
     */
    private void onVolumeChanged(Observable observable) {
        if (player != null) {
            player.setVolume(volumeBar.getValue()/100);
        }
    }

    /**
     * 播放或暂停音视频。
     * @param event 鼠标事件
     */
    private void onPlayOrPause(ActionEvent event) {
        if (player == null) {
            return;
        }

        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            playButton.setText(fontawsome5Service.getFontIcon("play"));
            player.pause();
        } else {
            playButton.setText(fontawsome5Service.getFontIcon("pause"));
            player.play();
        }
    }

    /**
     * 初始化音视频播放控件。
     */
    private void onMediaReady() {
        progressBar.setMin(0);
        progressBar.setMax(player.getTotalDuration().toSeconds());
        progressBar.setValue(0);
        playButton.setDisable(false);
        if (mediaView != null) {
            mediaView.setMediaPlayer(player);
        }
    }

    /**
     * 初始化视频播放控件。
     */
    public void previewVideo() {
        HBox mediaWrapper = new HBox();
        mediaWrapper.setAlignment(Pos.CENTER);

        mediaView = new MediaView();
        mediaView.setSmooth(true);
        mediaView.fitWidthProperty().bind(this.widthProperty());
        mediaView.fitHeightProperty().bind(this.heightProperty().subtract(controls.heightProperty().multiply(2)));
        mediaView.setPreserveRatio(true);

        mediaWrapper.getChildren().add(mediaView);
        mediaWrapper.getStyleClass().add("media-wrapper");
        root.setCenter(mediaWrapper);
    }

}
