package org.swdc.rmdisk.views.modals;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.core.LanguageKeys;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

@View(
        viewLocation = "views/modals/EditAvatar.fxml",
        dialog = true,
        title = LanguageKeys.UI_SERVER_DLG_EDIT_AVATAR_TITLE,
        multiple = true,
        resizeable = false
)
public class EditAvatarView extends AbstractView {

    private Canvas canvas;

    private Image image;

    private double x = 0;
    private double y = 0;

    private double offsetX = 0;
    private double offsetY = 0;

    private boolean cancelFlag = false;


    @PostConstruct
    public void init() {

        StackPane content = findById("content");

        canvas = new Canvas();
        canvas.setOnMousePressed(e -> {
            this.x = e.getX();
            this.y = e.getY();
        });

        canvas.setOnMouseDragged(e -> {

            GraphicsContext ctx = canvas.getGraphicsContext2D();
            ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            double offsetX = this.offsetX + (e.getX() - this.x);
            double offsetY = this.offsetY + (e.getY() - this.y);

            refreshScreen(ctx);
            if (!inBounds(offsetX, offsetY)) {
                this.x = e.getX();
                this.y = e.getY();
                return;
            }

            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.x = e.getX();
            this.y = e.getY();

        });

        canvas.setOnMouseReleased(e -> {
            this.x = 0;
            this.y = 0;
        });

        canvas.widthProperty().bind(content.widthProperty());
        canvas.heightProperty().bind(content.heightProperty());

        content.getChildren().add(canvas);

        Button submitButton = findById("btnSubmit");
        submitButton.setOnAction(this::onSubmit);
        Button cancelButton = findById("btnCancel");
        cancelButton.setOnAction(this::onCancel);

    }

    private void onCancel(ActionEvent e) {
        cancelFlag = true;
        hide();
    }

    private void onSubmit(ActionEvent e) {
        cancelFlag = false;
        hide();
    }

    /**
     * 检查给定的偏移量是否在图像的边界内。
     *
     * @param offsetX X轴偏移量
     * @param offsetY Y轴偏移量
     * @return 如果偏移量在图像边界内，则返回true；否则返回false。
     */
    private boolean inBounds(double offsetX, double offsetY) {

        double centerX = (canvas.getWidth() - 240) / 2;
        double centerY = (canvas.getHeight() - 240) / 2;

        double xPos = -offsetX;
        double yPos = -offsetY;

        if (xPos + centerX <= 0 || xPos + centerX + 240 >= image.getWidth()) {
            return false;
        }
        if (yPos + centerY <= 0 || yPos + centerY + 240 >= image.getHeight()) {
            return false;
        }
        return true;
    }


    /**
     * 渲染头像图片
     *
     * @return 渲染后的头像图片
     */
    private BufferedImage renderAvatar() {

        double centerX = (canvas.getWidth() - 240) / 2;
        double centerY = (canvas.getHeight() - 240) / 2;

        double xPos = -offsetX;
        double yPos = -offsetY;

        WritableImage writeableImage = new WritableImage(240,240);

        int srcXPos = (int)xPos + (int)centerX;
        int srcYPos = (int)yPos + (int)centerY;

        for (int x = srcXPos; x < srcXPos + 240;x++) {
            for (int y = srcYPos; y < srcYPos + 240; y++) {
                Color color = image.getPixelReader().getColor(x,y);
                writeableImage.getPixelWriter().setColor(x - srcXPos,y - srcYPos,color);
            }
        }

        return SwingFXUtils.fromFXImage(writeableImage,null);
    }

    /**
     * 刷新画布
     *
     * @param ctx 绘图上下文
     */
    private void refreshScreen(GraphicsContext ctx) {

        ctx.drawImage(image,offsetX,offsetY,image.getWidth(),image.getHeight());
        ctx.setFill(new Color(0,0,0,0.6f));
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = (canvas.getWidth() - 240) / 2;
        double centerY = (canvas.getHeight() - 240) / 2;
        double xPos = -offsetX;
        double yPos = -offsetY;

        ctx.drawImage(image,xPos + centerX,yPos + centerY,240,240,centerX,centerY,240,240);


    }

    public byte[] showModal(File file) {

        if (file == null) {
            return null;
        }

        try(FileInputStream fis = new FileInputStream(file)) {
            image = new Image(fis);
            offsetX =  0;
            offsetY = 0;

            Platform.runLater(() -> {
                refreshScreen(canvas.getGraphicsContext2D());
            });

            show();
            if (cancelFlag) {
                return null;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(renderAvatar(),"png",bos);
            return bos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

}
