# Tài liệu Phân tích Nghiệp vụ (Business Analysis) - Dự án Brainstorm Hub

* **Ngày cập nhật:** 12/07/2025
* **Phiên bản:** **1.1**
* **Người cập nhật:** Trợ lý ý tưởng

---

### Lịch sử thay đổi (Change Log)

| Phiên bản | Ngày       | Nội dung thay đổi                                                                       |
| :--------- | :--------- | :-------------------------------------------------------------------------------------- |
| 1.1        | 12/07/2025 | - Làm rõ cơ chế Authentication & Authorization bằng JWT.<br>- Bỏ yêu cầu mời qua email, thay bằng cơ chế mời qua link.<br>- Thêm vai trò Owner và Member trong Workspace. |
| 1.0        | 12/07/2025 | - Phiên bản ban đầu.                                                                    |

---

### 1. Tổng quan Dự án (Project Overview)

* **Tên dự án:** Brainstorm Hub
* **Tầm nhìn:** Xây dựng một nền tảng web hợp nhất, giúp người dùng và các nhóm nhỏ chuyển đổi liền mạch từ quá trình brainstorming (suy nghĩ ý tưởng) tự do, sáng tạo sang việc lập kế hoạch và quản lý công việc một cách có cấu trúc.
* **Vấn đề giải quyết:** Các công cụ hiện tại thường chỉ mạnh về một trong hai mặt: hoặc là công cụ whiteboard (như Miro, Mural) hoặc là công cụ quản lý tác vụ (như Trello, Asana). Người dùng phải chuyển đổi giữa nhiều ứng dụng, gây ra sự rời rạc trong luồng công việc từ ý tưởng đến hành động. Brainstorm Hub giải quyết vấn đề này bằng cách tích hợp cả hai chức năng vào một nền tảng duy nhất.

---

### 2. Phạm vi Dự án (Project Scope)

#### Trong phạm vi (In-Scope):

* Xác thực và quản lý tài khoản người dùng (Đăng ký, Đăng nhập, Đăng xuất) **sử dụng JWT**.
* Tạo và quản lý các không gian làm việc (Workspace).
* **Phân quyền người dùng trong Workspace (Owner, Member).**
* **Mời thành viên khác vào Workspace qua một đường link mời duy nhất.**
* Module "Flow" (Quản lý công việc) với bảng Kanban.
* Module "Board" (Bảng trắng sáng tạo) với tính năng cộng tác thời gian thực.
* Tích hợp chuyển đổi đối tượng từ "Board" thành công việc trong "Flow".

#### Ngoài phạm vi (Out-of-Scope) cho Phiên bản 1.0:

* Ứng dụng di động Native (Android/iOS).
* **Tích hợp dịch vụ email để gửi thông báo hoặc lời mời.**
* Các vai trò phân quyền phức tạp hơn (vd: editor, viewer).
* Chức năng chat/bình luận.
* Chế độ hoạt động ngoại tuyến (Offline mode).

---

### 3. Đối tượng Người dùng và Phân quyền (User Roles & Authorization)

Hệ thống có một loại người dùng là **Người dùng đã đăng ký**. Tuy nhiên, trong phạm vi một Workspace, họ có thể có một trong hai vai trò sau:

* **Chủ sở hữu (Owner):**
    * Là người tạo ra Workspace.
    * Có toàn quyền trong Workspace: sử dụng các chức năng của "Flow" và "Board".
    * **Có thể tạo link mời, xóa thành viên khác và xóa toàn bộ Workspace.**
* **Thành viên (Member):**
    * Là người tham gia Workspace qua lời mời.
    * Có quyền sử dụng các chức năng của "Flow" và "Board" (tạo/sửa task, vẽ trên board).
    * **Không thể xóa Workspace hay xóa các thành viên khác.**

---

### 4. Yêu cầu Chức năng (Functional Requirements)

| ID     | Module               | Tính năng                 | Mô tả chi tiết                                                                                                                                              |
| :----- | :------------------- | :------------------------ | :---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **FN-1** | Quản lý Người dùng   | Đăng ký                   | Người dùng có thể tạo tài khoản mới bằng email và mật khẩu.                                                                                                |
| **FN-2** |                      | Đăng nhập                 | Người dùng đăng nhập bằng email và mật khẩu. **Hệ thống trả về một JWT (JSON Web Token) để xác thực cho các phiên làm việc tiếp theo.** |
| **FN-3** | Quản lý Workspace    | Tạo Workspace             | Người dùng có thể tạo một Workspace mới. Người tạo mặc định là **Owner**.                                                                                  |
| **FN-4** |                      | Mời thành viên            | **(Đã sửa đổi)** **Owner** của Workspace có thể tạo một đường link mời duy nhất. Owner sẽ tự chia sẻ link này cho người khác. Người dùng khác khi bấm vào link sẽ trở thành **Member** của Workspace. |
| **FN-5** | **Module "Flow"** | Hiển thị bảng Kanban      | Hiển thị các công việc trong các cột: "To Do", "In Progress", "Done".                                                                                     |
| **FN-6** |                      | Tạo công việc             | Người dùng có thể tạo công việc mới (thẻ task) trong một cột.                                                                                              |
| **FN-7** |                      | Kéo và thả                | Người dùng có thể thay đổi trạng thái công việc bằng cách kéo thả thẻ giữa các cột.                                                                        |
| **FN-8** |                      | Chế độ Focus              | Bật chế độ Pomodoro cho một công việc cụ thể để tập trung làm việc.                                                                                        |
| **FN-9** | **Module "Board"** | Tạo Bảng trắng            | Người dùng có thể tạo một hoặc nhiều bảng trắng trong một Workspace.                                                                                       |
| **FN-10** |                      | Cộng tác thời gian thực   | Khi một người dùng vẽ hoặc di chuyển đối tượng, những người khác trong cùng bảng sẽ thấy thay đổi ngay lập tức (<500ms).                                       |
| **FN-11** | **Tích hợp** | Chuyển đổi Ý tưởng        | Người dùng có thể nhấp chuột phải vào một sticky note trên "Board" và chọn "Chuyển thành công việc" để tạo một thẻ task tương ứng trong "Flow".                |
| **FN-12** | **Phân quyền** | Quản lý thành viên        | **Owner** có thể xem danh sách thành viên và xóa một **Member** khỏi Workspace.                                                                           |
| **FN-13** |                      | Xóa Workspace             | Chỉ **Owner** mới có quyền xóa toàn bộ Workspace.                                                                                                          |

---

### 5. Yêu cầu Phi chức năng (Non-Functional Requirements)

| ID      | Yêu cầu          | Mô tả chi tiết                                                                                                                                                                             |
| :------ | :--------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **NFR-1** | **Hiệu suất** | Thời gian tải trang chính phải dưới 2 giây. Các hành động cộng tác thời gian thực phải được phản ánh dưới 500ms.                                                                              |
| **NFR-2** | **Bảo mật** | Mật khẩu người dùng phải được băm (hashed). Giao tiếp client-server phải qua HTTPS. **Mọi API yêu cầu đăng nhập phải được bảo vệ và xác thực bằng JWT. Backend phải thực thi kiểm tra quyền (Authorization) dựa trên vai trò của người dùng trong Workspace (Owner/Member) trước khi thực hiện hành động.** |
| **NFR-3** | **Tính khả dụng** | Giao diện phải responsive, hoạt động tốt trên các trình duyệt web hiện đại (Chrome, Firefox, Safari) trên máy tính để bàn.                                                                    |
| **NFR-4** | **Công nghệ** | Backend: Kotlin (Spring Boot/Ktor). Frontend: React + TypeScript. CSDL: PostgreSQL.                                                                                                      |

---

### 6. Mô hình Dữ liệu Sơ bộ (Preliminary Data Model)

* **Users:** `user_id`, `email`, `password_hash`, `full_name`, `created_at`
* **Workspaces:** `workspace_id`, `name`, `owner_id` (FK to Users)
* **Workspace\_Members:** `workspace_id` (FK), `user_id` (FK), **`role` (ENUM: 'owner', 'member')**
* **Tasks:** `task_id`, `workspace_id` (FK), `title`, `description`, `status`, `assignee_id` (FK to Users), `due_date`
* **Boards:** `board_id`, `workspace_id` (FK), `name`
* **Board\_Objects:** `object_id`, `board_id` (FK), `type` (sticky\_note, shape, text), `position_x`, `position_y`, `width`, `height`, `content` (JSON)
* **Invite\_Links:** `link_id`, `workspace_id` (FK), `token` (chuỗi duy nhất), `expires_at`, `is_active`
