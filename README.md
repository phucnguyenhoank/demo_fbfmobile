## 📱 FastBreakfast – Ứng dụng đặt và giao bữa sáng nhanh chóng
**FastBreakfast** là ứng dụng di động cung cấp dịch vụ đặt và giao bữa sáng nhanh chóng, tiện lợi, nhằm đáp ứng nhu cầu ăn sáng chất lượng trong thời gian ngắn của người dùng – đặc biệt là học sinh, sinh viên và nhân viên văn phòng. Với tiêu chí **nhanh – gọn – tiện**, 
ứng dụng cho phép người dùng lựa chọn món ăn từ thực đơn phong phú, đặt hàng dễ dàng, thanh toán nhanh chóng, góp phần tiết kiệm thời gian và nâng cao trải nghiệm sử dụng.
## ⚙️ Đặc tả hệ thống (Technical Specification)

### 🔗 Mối quan hệ dữ liệu

* **Food** phải thuộc về **duy nhất một Category** và có các biến thể về **FoodSize**. Mỗi `FoodSize` đi kèm kích thước (S, M, L, XL) và giá tương ứng.
* **CartItem** đại diện cho một món ăn được thêm vào giỏ hàng. Một `CartItem` là sự kết hợp giữa `Food`, `FoodSize`, `quantity`, và `price`.
* `CartItem` có thể hiển thị giá đã giảm nhờ **DiscountCode** (chỉ hiển thị, không áp dụng thực sự cho dữ liệu).
* Sau khi tạo đơn hàng, `CartItem` được chuyển thành `OrderItem` – khác biệt duy nhất là `OrderItem` mang giá thanh toán cuối cùng (có thể đã giảm).
* Một **FbfUser** có duy nhất **1 Cart**, và **1 Cart** thuộc về duy nhất **1 FbfUser**.
* Mỗi **Cart** chứa 0 hoặc nhiều **CartItem**, và mỗi **CartItem** phải thuộc về một **Cart** cụ thể.
* Một **FbfOrder** chứa 1 hoặc nhiều **OrderItem**.
* Một **FbfUser** có thể tạo và thanh toán nhiều **FbfOrder**.
* **DiscountCode**:

  * Có thể áp dụng cho `OrderItem` (sau khi tạo từ `CartItem`).
  * Có thể áp dụng trực tiếp lên `FbfOrder` bởi hệ thống hoặc người dùng.
  * Được tặng cho người dùng tự động dựa trên lịch sử mua hàng.
---
## 🔧 Chức năng chính

### 1. Xác thực người dùng

* Người dùng có thể xem hàng mà không cần đăng nhập.
* Đăng ký tài khoản bằng tên đăng nhập, mật khẩu, họ tên, số điện thoại, địa chỉ và email.
* Xác thực đăng ký bằng mã OTP gửi qua email.
* Hỗ trợ đăng nhập, đăng xuất (xoá token), và khôi phục mật khẩu qua email.

### 2. Tìm kiếm món ăn

* Cho phép tìm món ăn theo tên mà không cần đăng nhập.
* Kết quả hiển thị rõ ràng với hình ảnh, giá và mô tả, được phân trang dễ nhìn.

### 3. Lọc và sắp xếp món ăn

* Lọc theo khoảng giá hoặc theo loại (Snacks, Meal, Vegan, Dessert, Drink).
* Sắp xếp theo mã sản phẩm hoặc tên theo thứ tự tăng/giảm.

### 4. Quản lý giỏ hàng

* Xem chi tiết món ăn, chỉnh sửa kích thước và số lượng.
* Thêm vào giỏ hàng sau khi đăng nhập.
* Cho phép xem, cập nhật hoặc xóa mục trong giỏ.
* Hiển thị giá ước tính rõ ràng và tạo đơn hàng nhanh chóng.

### 5. Thêm món ăn vào danh mục yêu thích

* Lưu món ăn vào danh sách yêu thích (lưu cục bộ trên thiết bị).
* Dễ dàng quản lý hoặc xem lại sau mà không cần cho vào giỏ hàng.

### 6. Đặt hàng và thanh toán

* Tạo đơn hàng từ các mục trong giỏ hàng, kèm chi tiết giá, số lượng, kích thước,...
* Hỗ trợ nhập mã giảm giá cho đơn hàng.
* Đơn hàng có thời gian thanh toán là **3 phút**, sau đó sẽ bị huỷ nếu chưa thanh toán.

### 7. Thanh toán sau

* Trong 3 phút chờ thanh toán, người dùng có thể thoát ứng dụng và quay lại thanh toán sau.
* Sau thời gian này, đơn hàng sẽ tự động chuyển sang trạng thái **CANCELED**.

### 8. Xem lịch sử hóa đơn

* Hóa đơn có 3 trạng thái: `PENDING`, `PAID`, `CANCELED`.
* Cho phép thanh toán lại đơn hàng đang ở trạng thái `PENDING`.
* Không thể thao tác lại với hóa đơn bị huỷ.

### 9. Xem chi tiết hóa đơn đã thanh toán

* Các hóa đơn `PAID` có thể xem chi tiết từng món: tên, kích thước, số lượng, đơn giá, giảm giá,...

### 10. Xem, cập nhật thông tin cá nhân

* Thông tin cá nhân gồm: tên, số điện thoại, địa chỉ, email.
* Dữ liệu được dùng để tự động điền khi tạo đơn hàng, nhưng có thể chỉnh sửa khi cần.
* Có nút đăng xuất để xoá token khỏi thiết bị.

---

Bạn muốn thêm phần hướng dẫn cài đặt, kiến trúc hệ thống, hoặc API backend cho phần `README.md` không?
