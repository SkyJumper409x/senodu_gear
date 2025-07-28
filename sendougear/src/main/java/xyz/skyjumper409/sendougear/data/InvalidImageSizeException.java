package xyz.skyjumper409.sendougear.data;

public class InvalidImageSizeException extends RuntimeException {
    private BiInt size;
    private String messageAdd;
    public InvalidImageSizeException(BiInt size) {
        super();
        setImageSize(size);
    }
    public InvalidImageSizeException(String message, BiInt size) {
        super(message);
        setImageSize(size);
    }
    public InvalidImageSizeException(Throwable cause, BiInt size) {
        super(cause);
        setImageSize(size);
    }
    public InvalidImageSizeException(String message, Throwable cause, BiInt size) {
        super(message, cause);
        setImageSize(size);
    }
    private void setImageSize(BiInt size) {
        this.size = size.unmodifiableClone();
        messageAdd = " (resolution received was " + this.size.x() + "x" + this.size.y() + ")";
    }
    public BiInt getImageSize() {
        return size;
    }
    @Override
    public String getMessage() {
        return super.getMessage() + messageAdd;
    }
}
