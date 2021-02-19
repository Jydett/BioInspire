package fr.polytech.jydet.reactdiffuse;

import fr.polytech.jydet.utils.GifSequenceWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.polytech.jydet.reactdiffuse.ReactDiffuse.deepCopy;

public class ReactDiffuseGifProxy {

    @Getter
    ReactDiffuseModel reactDiffuseModel;
    private BufferedImage[] images;

    public ReactDiffuseGifProxy(ReactDiffuseModel reactDiffuseModel, int round) {
        this.reactDiffuseModel = reactDiffuseModel;
        images = new BufferedImage[round];
    }

    public void tickFull() {
        for (int i = 0; i < images.length; i++) {
            images[i] = tick();
        }
    }

    public void produceGif(String name) {
        GifSequenceWriter writer;
        try(FileImageOutputStream outputStream = new FileImageOutputStream(new File(name))) {
            writer = new GifSequenceWriter(outputStream,
                BufferedImage.TYPE_INT_ARGB, 500, true);

            writer.writeToSequence(images[0]);
            for(int i=1; i <images.length-1; i++) {
                writer.writeToSequence(images[i]);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage tick() {
        reactDiffuseModel.update();
        return deepCopy(reactDiffuseModel.getModel());
    }
}
