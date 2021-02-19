package fr.polytech.jydet.reactdiffuse;

import fr.polytech.jydet.utils.GifSequenceWriter;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static fr.polytech.jydet.reactdiffuse.ReactDiffuse.deepCopy;

public class ReactDiffuseImageProxy {

    @Getter
    ReactDiffuseModel reactDiffuseModel;
    private int round;

    public ReactDiffuseImageProxy(ReactDiffuseModel reactDiffuseModel, int round) {
        this.reactDiffuseModel = reactDiffuseModel;
        this.round = round;
    }

    public void tickFullAndSave(int id) throws IOException {
        for (int i = 0; i < round; i++) {
            tick();
        }
        ImageIO.write(deepCopy(reactDiffuseModel.getModel()), "png", new File(id + ".png"));
    }

    public void tick() {
        reactDiffuseModel.update();
    }
}
