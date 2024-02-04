package ariefsyaifu.dto.voucher.backoffice;

import ariefsyaifu.model.Voucher;

public class ViewVoucherIdOas {
    public String id;

    public static ViewVoucherIdOas valueOf(Voucher v) {
        ViewVoucherIdOas oas = new ViewVoucherIdOas();
        oas.id = v.id;
        return oas;
    }
}
