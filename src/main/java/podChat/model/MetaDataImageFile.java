package podChat.model;

public class MetaDataImageFile {
    private FileImageMetaData file;
    private String name;
    private long id;

    public FileImageMetaData getFile() {
        return file;
    }

    public void setFile(FileImageMetaData file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
