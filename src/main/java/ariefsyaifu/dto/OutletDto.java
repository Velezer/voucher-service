package ariefsyaifu.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import ariefsyaifu.model.VoucherOutlet;

public class OutletDto {
    @Schema(example = "TOKOKU")
    public String id;
    public String name;

    public static OutletDto valueOf(VoucherOutlet o) {
        OutletDto dto = new OutletDto();
        dto.id = o.outletId;
        dto.name = o.outletName;
        return dto;
    }
}
