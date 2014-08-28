package models.metabg;

import com.google.common.base.Optional;

public class Resource
{
    private final String nameFront;
    private final String imageFront;
    private final int width;
    private final int height;
    private final Optional<String> nameBack;
    private final Optional<String> imageBack;

    public Resource (String name, String image, int width, int height) {
        this.nameFront = name;
        this.imageFront = image;
        this.width = width;
        this.height = height;
        this.nameBack = Optional.absent();
        this.imageBack = Optional.absent();
    }    
    
    public Resource (String nameFront, String imageFront, int width, int height, String nameBack) {
        this.nameFront = nameFront;
        this.imageFront = imageFront;
        this.width = width;
        this.height = height;
        this.nameBack = Optional.of(nameBack);
        this.imageBack = Optional.absent();
    }

    public Resource (String nameFront, String imageFront, int width, int height, String nameBack, String imageBack) {
        this.nameFront = nameFront;
        this.imageFront = imageFront;
        this.width = width;
        this.height = height;
        this.nameBack = Optional.of(nameBack);
        this.imageBack = Optional.of(imageBack);
    }
    
    public String getNameFront () { return nameFront; }
    public String getImageFront () { return imageFront; }
    public int getWidth () { return width; }
    public int getHeight () { return height; }
    public boolean hasBack () { return nameBack.isPresent(); }
    public String getNameBack () { return nameBack.get(); }
    public String getImageBack () { return imageBack.get(); }
}
