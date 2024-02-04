package ariefsyaifu.dto;

public class ViewCountOas {
    public long count;

    public static ViewCountOas valueOf(long count) {
        ViewCountOas oas = new ViewCountOas();
        oas.count = count;
        return oas;
    }
}
