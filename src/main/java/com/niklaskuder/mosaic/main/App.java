package com.niklaskuder.mosaic.main;

import com.niklaskuder.mosaic.base.MosaicArtist;
import com.niklaskuder.mosaic.base.MosaicEasel;
import com.niklaskuder.mosaic.rectangle.RectangleMosaicArtist;
import com.niklaskuder.mosaic.rectangle.RectangleMosaicEasel;
import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class parses all command line parameters and creates a mosaic.
 */
public final class App {

    private App() {
        throw new IllegalAccessError();
    }

    private static final String CMD_OPTION_INPUT_IMAGE = "i";
    private static final String CMD_OPTION_INPUT_TILES_DIR = "t";
    private static final String CMD_OPTION_OUTPUT_IMAGE = "o";

    private static final String CMD_OPTION_TILE_W = "w";
    private static final String CMD_OPTION_TILE_H = "h";

    public static void main(String[] args) {
        // Don't touch...
        CommandLine cmd = null;
        try {
            cmd = App.doCommandLineParsing(args);
        } catch (ParseException e) {
            System.err.println("Wrong command line arguments given: " + e.getMessage());
            System.exit(1);
        }
        // ...this!

        BufferedImage inputImage;
        File picDirectory;
        List<BufferedImage> mosaicImages = new ArrayList<>();
        //Load all the images
        try {
            inputImage = ImageIO.read(new File(cmd.getParsedOptionValue(CMD_OPTION_INPUT_IMAGE).toString()));
            picDirectory = new File(cmd.getParsedOptionValue(CMD_OPTION_INPUT_TILES_DIR).toString());
            if (picDirectory.isDirectory() && Objects.requireNonNull(picDirectory.listFiles()).length >= 10) {
                for (File file : Objects.requireNonNull(picDirectory.listFiles())) {
                    mosaicImages.add(ImageIO.read(file));
                    System.out.println("Reading file: " + file.getName());
                }
            } else {
                System.out.println("Not a directory, or not at least 10 images.");
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return;
        }
        //Check and set the width and height of the rectangle shapes
        int prefWidth;
        int prefHeight;
        try {
            prefWidth = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_W));
            prefHeight = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_H));
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("No input for width or height given.");
            prefHeight = 10;
            prefWidth = 10;
        }
        int width = 0;
        int height = 0;
        if (prefWidth > 0 && prefWidth <= inputImage.getWidth()) {
            width = prefWidth;
        }
        if (prefHeight > 0 && prefHeight <= inputImage.getHeight()) {
            height = prefHeight;
        }
        //Create Easel and Artist
        MosaicEasel easel = new RectangleMosaicEasel();
        MosaicArtist artist = new RectangleMosaicArtist(mosaicImages, width, height);

        //Create the mosaic image and write it to the output file
        try {
            System.out.println("Creating image...");
            BufferedImage outputImage = easel.createMosaic(inputImage, artist);
            String storeInput = cmd.getParsedOptionValue(CMD_OPTION_OUTPUT_IMAGE).toString();
            File outputFile = new File(storeInput);
            ImageIO.write(outputImage, "png", outputFile);
            System.out.println("Picture saved to " + outputFile.getAbsolutePath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse and check command line arguments
     *
     * @param args command line arguments given by the user
     * @return CommandLine object encapsulating all options
     * @throws ParseException if wrong command line parameters or arguments are given
     */
    private static CommandLine doCommandLineParsing(String[] args) throws ParseException {
        Options options = new Options();
        Option opt;

        /*
         * Define command line options and arguments
         */
        opt = new Option(App.CMD_OPTION_INPUT_IMAGE, "input-images", true, "path to input image");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(App.CMD_OPTION_INPUT_TILES_DIR, "tiles-dir", true, "path to tiles directory");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(App.CMD_OPTION_OUTPUT_IMAGE, "image-output", true, "path to output image");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(App.CMD_OPTION_TILE_W, "tile-width", true, "the width of a tile");
        opt.setRequired(false);
        opt.setType(Integer.class);
        options.addOption(opt);

        opt = new Option(App.CMD_OPTION_TILE_H, "tile-height", true, "the height of a tile");
        opt.setRequired(false);
        opt.setType(Integer.class);
        options.addOption(opt);

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

}
