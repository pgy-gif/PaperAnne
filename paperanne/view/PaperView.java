package com.paperanne.view;

import com.paperanne.model.PaperModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class PaperView {
    private final PaperModel paperModel;
    private final ImageView imageView;

    public PaperView(PaperModel paperModel, String imagePath) {
        this.paperModel = paperModel;
        this.imageView = new ImageView();

        // 加载图片
        Image paperImage = new Image(getClass().getResourceAsStream(imagePath));
        imageView.setImage(paperImage);

        // 绑定模型数据到视图
        setupBindings();
    }

    private void setupBindings() {
        // 位置绑定
        imageView.xProperty().bind(paperModel.xProperty());
        imageView.yProperty().bind(paperModel.yProperty());

        // 大小绑定
        imageView.fitWidthProperty().bind(paperModel.widthProperty());
        imageView.fitHeightProperty().bind(paperModel.heightProperty());

        // 旋转绑定
        imageView.rotateProperty().bind(paperModel.rotationProperty());

        // 拖动状态绑定透明度
        imageView.opacityProperty().bind(
                javafx.beans.binding.Bindings.when(paperModel.draggingProperty())
                        .then(0.8)
                        .otherwise(1.0)
        );
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void addToLayer(Pane layer) {
        layer.getChildren().add(imageView);
    }
}
