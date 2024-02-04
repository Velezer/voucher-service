package ariefsyaifu.dto;

import ariefsyaifu.model.VoucherOutlet;

public class OutletDto {
    public String id;
    public String name;

    public static OutletDto valueOf(VoucherOutlet o) {
        OutletDto dto = new OutletDto();
        dto.id = o.outletId;
        dto.name = o.outletName;
        return dto;
    }
}
