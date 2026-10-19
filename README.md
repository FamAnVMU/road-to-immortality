# Road to Immortality (Con Đường Tu Tiên) - Minecraft 1.21.1 NeoForge

Hệ thống RPG Tu Tiên / Tiên Hiệp (Xianxia Cultivation) hoàn chỉnh, server-authoritative, cân bằng và có chiều sâu dành cho Minecraft phiên bản **1.21.1**, mod loader **NeoForge**, ngôn ngữ **Java 21**.

### Hỗ Trợ Đa Ngôn Ngữ (i18n):
Mod hỗ trợ song ngữ 100% không dùng chuỗi cứng (hardcoded text), tự động chuyển đổi theo ngôn ngữ người chơi chọn trong cài đặt Minecraft:
- **Tiếng Việt (`vi_vn.json`)**: Văn phong tu chân, tiên hiệp phương Đông thuần thục (Luyện Khí, Trúc Cơ, Kim Đan, Linh Thạch, Tụ Linh Trận, v.v.).
- **English (`en_us.json`)**: Authentic Eastern Cultivation / Xianxia terminology (Qi Condensation, Foundation Establishment, Golden Core, Nascent Soul, Dantian, Spirit Stones, Qi Gathering Formation, etc.).

---

## I. TỔNG QUAN HỆ THỐNG VÀ TÍNH NĂNG ĐÃ TRIỂN KHAI

### 1. Hệ Thống Tu Vi và 9 Đại Cảnh Giới
- **9 Cảnh Giới**:
  1. Luyện Khí
  2. Trúc Cơ
  3. Kim Đan
  4. Nguyên Anh
  5. Hóa Thần
  6. Động Hư
  7. Độ Kiếp
  8. Vũ Hóa
  9. Đăng Tiên
- Mỗi cảnh giới có **9 tầng** (Tổng cộng **81 tầng tu vi**).
- **Cơ chế Củng Cố Căn Cơ**: Sau khi đột phá tầng mới hoặc cảnh giới mới, căn cơ cần được củng cố thông qua ngồi thiền (từ 20-50% lên 100%). Căn cơ chưa củng cố sẽ giới hạn thuộc tính cộng thêm.
- **Cơ chế Đột Phá**:
  - Tầng 1 đến 8: Yêu cầu tích lũy đủ lượng tu vi.
  - Tầng 9: Bắt buộc phải có đúng loại **Đan Dược Đột Phá** tương ứng (Trúc Cơ Đan, Kim Đan Đan, Nguyên Anh Đan, Hóa Thần Đan, Động Hư Đan, Độ Kiếp Đan, Vũ Hóa Đan, Đăng Tiên Đan) trong kho đồ.
  - Giao diện xác nhận đột phá (Phím `K` hoặc bảng trạng thái), kiểm tra an toàn trên server, không tiêu hao đan dược nếu chưa bấm xác nhận.

### 2. Tu Luyện và Phím Tắt C
- **Phím mặc định `C`**: Đăng ký trong Key Mapping của Minecraft, có thể tùy chỉnh.
- Nhập định ngồi thiền, hấp thu linh khí đất trời, gia tăng tu vi theo thời gian thực và củng cố căn cơ.
- **Cơ chế tự động hủy an toàn**: Tự động kết thúc tu luyện khi di chuyển, bị quái vật tấn công, tử vong hoặc mất kết nối.

### 3. Tiền Tệ: 4 Phẩm Cấp Linh Thạch
- **4 Phẩm Chất**:
  - Hạ Phẩm Linh Thạch (Tỷ lệ 1)
  - Trung Phẩm Linh Thạch (1.000 Hạ Phẩm = 1 Trung Phẩm)
  - Thượng Phẩm Linh Thạch (1.000 Trung Phẩm = 1 Thượng Phẩm)
  - Thần Phẩm Linh Thạch (1.000 Thượng Phẩm = 1 Thần Phẩm = 1.000.000.000 Hạ Phẩm)
- **Gương Quy Đổi Linh Thạch (`linh_thach_quy_doi_item`)**: Mở giao diện quy đổi tiền tệ mọi lúc, đổi 2 chiều an toàn, chống duplicate (nhân đôi) và chống tràn số với cơ chế giao dịch nguyên tử (atomic).

### 4. Công Trình và Bản Vẽ Litematica
- Tích hợp bộ nạp và giải mã bản vẽ Litematica trực tiếp trong game (`LitematicParser`):
  - `structures/Thị trấn.litematic`
  - `structures/Thương Vân Tông.litematic`
  - `structures/Côn Luận Thánh Địa.litematic`
- Lệnh: `/tutien structure spawn <thitran|thuongvan|conluan> [x y z]`
- Tự động thiết lập đầy đủ các NPC chưởng quản và các tiện ích (Lò luyện đan cố định, Lò luyện khí cố định, Tế đàn hồi phục).
- **Bảo vệ công trình**: Các lò luyện đan, lò luyện khí và tế đàn tại công trình được bảo vệ tuyệt đối, người chơi không thể phá hủy và tương thích chống bị Carry On nhấc đi.

### 5. NPC, Giao Dịch, Tông Môn và Điểm Số
- **Hệ thống NPC chuyên biệt**:
  - Chưởng Quầy Đan Dược
  - Đan Sư Tông Sư (Bán đan phương & dịch vụ)
  - Thần Binh Các Chủ (Bán pháp bảo)
  - Luyện Khí Đại Sư (Bán bản vẽ)
  - Tàng Kinh Các Trưởng Lão (Bán công pháp)
  - Chấp Sự Thị Trấn (Giao nhiệm vụ, thưởng công huân & linh thạch)
  - Thương Vân Trưởng Lão (Trao đổi pháp bảo & công pháp Thương Vân Tông)
  - Côn Luân Thái Thượng (Trao đổi thần binh & bí pháp Côn Luân Thánh Địa)
- **Phân biệt hai loại điểm**:
  - **Công Huân**: Dùng cho thị trấn và các nhiệm vụ trung lập.
  - **Cống Hiến Tông Môn**: Dùng để đổi bảo vật độc quyền tại Thương Vân Tông và Côn Luân Thánh Địa.

### 6. Công Pháp, Linh Khí và Chiến Đấu
- **4 Cấp Bậc**: Hoàng Cấp, Huyền Cấp, Địa Cấp, Thiên Cấp.
- **4 Loại Hình**:
  - Võ Kỹ: Thanh Phong Kiếm Quyết, Liệt Hỏa Chưởng
  - Thân Pháp: Lăng Ba Vi Bộ, Hư Không Bộ (Blink)
  - Tuyệt Kỹ: Thiên Lôi Quyết, Huyền Băng Kiếm Trận
  - Bí Pháp: Vạn Kiếm Quy Tông, Thái Hư Thần Lôi
- **Tài Nguyên Linh Khí (Qi)**: Thanh linh khí hiển thị trực quan trên màn hình (HUD), tự động hồi phục theo thời gian.
- **4 Phím Kỹ Năng Nhanh**: `Z`, `X`, `V`, `R`.
- **Độ Thành Thạo**: Tiểu Thành ➔ Đại Thành ➔ Viên Mãn ➔ Đại Viên Mãn. Càng dùng nhiều hoặc tiêu diệt quái vật càng tăng uy lực sát thương và giảm tiêu hao linh khí.

### 7. Luyện Đan, Linh Dược và Đất Linh Điền
- **Lò Luyện Đan (`lo_luyen_dan`)**: 9 cấp độ nâng cấp tuần tự.
- **9 Phẩm Cấp Linh Dược**: Linh Thảo, Thanh Tâm Hoa, Huyền Băng Thảo, Xích Hỏa Quả, Ngũ Hành Chi, U Hồn Hoa, Thiên Linh Quả, Cửu Diệp Thảo, Nguyên Thần Thảo.
- **3 Cấp Đất Linh Dược**: Hạ cấp, Trung cấp, Thượng cấp.
- **Bón Thúc Linh Thạch**: Nhấp chuột phải bằng Linh Thạch vào đất linh điền để tăng tốc sinh trưởng cho linh dược.
- **Đan Phương**: Vật phẩm nghiên cứu mở khóa công thức và nhận EXP Luyện Đan.

### 8. Luyện Khí và Pháp Bảo
- **Lò Luyện Khí (`lo_luyen_khi`)**: 4 cấp bậc (Hoàng, Huyền, Địa, Thiên).
- **Vũ Khí**: Thanh Phong Kiếm, Huyền Thiết Kiếm, Xích Diệm Kiếm, Thái Hư Kiếm (có kỹ năng phóng thích kiếm khí khi nhấp chuột phải).
- **Giáp Trụ**: Bộ Đạo Bào Hoàng, Huyền, Địa, Thiên Cấp (tăng hồi linh khí và phòng ngự).
- **Pháp Khí Phụ Trợ**: Hộ Tâm Kính (hộ thể khi thấp máu), Chiếu Yêu Kính (soi rọi sinh vật xung quanh), Phiên Thiên Ấn (đập chấn động diện rộng), Tru Tiên Trận Đồ (kiếm trận sát phạt).

### 9. Trận Pháp Tu Luyện
- **Tụ Linh Trận (`tran_phap_core`)**: 9 cấp độ.
- Tự động tính toán bán kính (5 đến 15 ô) và hệ số tăng tốc tu luyện (1.5x đến 10.0x).
- Thuật toán kiểm tra trên server, tự động hủy hệ số khi bước ra ngoài và chống chồng dồn vô hạn.

### 10. Tế Đàn Hồi Phục
- **Tế Đàn (`te_dan_hoi_phuc`)**: Tiêu hao lượng linh thạch theo cấp độ để hồi phục 100% Khí Huyết (HP), 100% Linh Khí (Qi) và tẩy trừ toàn bộ hiệu ứng bất lợi.

### 11. Quái Vật, Boss và Loot
- Quái vật thông thường rơi Hạ Phẩm Linh Thạch (tỷ lệ 45%).
- Boss (Wither, Ender Dragon) rơi Thượng Phẩm Linh Thạch và đan dược đột phá quý hiếm.
- Tương thích Lootr và các rương dungeon.

### 12. Tương Thích Mod
- **JEI (Just Enough Items)**: Đăng ký catalyst và hỗ trợ hiển thị công thức Luyện Đan, Luyện Khí.
- **Carry On**: Đã tạo tag `data/carryon/tags/block/block_blacklist.json` ngăn Carry On di chuyển lò và tế đàn cố định.
- **Curios API**: Đã cấu hình tag slot `charm` và `body` cho pháp khí và đạo bào.
- **Mouse Tweaks / Overflowing Bars**: Thiết kế giao diện và thanh Linh Khí tương thích cao.

---

## II. HƯỚNG DẪN CÀI ĐẶT VÀ BIÊN DỊCH

### 1. Yêu Cầu Môi Trường
- **Minecraft**: 1.21.1
- **Mod Loader**: NeoForge 21.1.65+
- **Java**: JDK 21 (LTS)

### 2. Cài Đặt File Mod Sẵn Có
- File JAR đã biên dịch hoàn tất nằm tại:
  `build/libs/tutien-1.0.0.jar`
- Sao chép file `tutien-1.0.0.jar` vào thư mục `.minecraft/mods` của bạn.

### 3. Tự Biên Dịch Từ Mã Nguồn
Mở terminal PowerShell trong thư mục dự án và chạy:
```powershell
cmd /c "set JAVA_HOME=C:\Program Files\Java\jdk-21&& gradlew.bat build -x test"
```
File JAR đầu ra sẽ được tạo tại `build/libs/tutien-1.0.0.jar`.

---

## III. DANH SÁCH LỆNH QUẢN TRỊ (COMMANDS)

- `/tutien structure spawn thitran`: Tạo Thị Trấn tại vị trí hiện tại.
- `/tutien structure spawn thuongvan`: Tạo Thương Vân Tông.
- `/tutien structure spawn conluan`: Tạo Côn Luân Thánh Địa.
- `/tutien tuvi add <amount>`: Thêm lượng tu vi cho bản thân.
- `/tutien realm set <realmId> [layer]`: Đặt cảnh giới (0 = Luyện Khí đến 8 = Đăng Tiên, layer = 1..9).
- `/tutien qi restore`: Hồi phục đầy đủ linh khí.

---

## IV. PHÍM TẮT MẶC ĐỊNH
- **Phím `C`**: Bật/Tắt chế độ Ngồi Thiền Tu Luyện.
- **Phím `K`**: Mở Bảng Trạng Thái Tu Tiên & Đột Phá.
- **Phím `Z`**: Thi triển Kỹ Năng Ô 1.
- **Phím `X`**: Thi triển Kỹ Năng Ô 2.
- **Phím `V`**: Thi triển Kỹ Năng Ô 3.
- **Phím `R`**: Thi triển Kỹ Năng Ô 4.
