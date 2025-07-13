Danh sách API Backend - Dự án Brainstorm Hub
Danh sách API Backend - Dự án Brainstorm Hub
Ngày cập nhật: 13/07/2025Phiên bản: 1.0Người soạn: Trợ lý ý tưởng

1. Tổng quan
   Danh sách này liệt kê các API backend cần thiết để triển khai các tính năng của Brainstorm Hub, một nền tảng tích hợp brainstorming và quản lý công việc. Các API sử dụng RESTful với JWT cho xác thực/phân quyền, giao tiếp qua HTTPS, và được thiết kế để tương thích với backend Kotlin (Spring Boot/Ktor), cơ sở dữ liệu PostgreSQL, và hỗ trợ cộng tác thời gian thực qua WebSocket.

2. API Quản lý Người dùng (User Management)
   API-1: Đăng ký

Mô tả: Tạo tài khoản người dùng mới.
Phương thức: POST
Endpoint: /api/v1/users/register
Đầu vào (Body): {
"email": "string",
"password": "string",
"full_name": "string"
}

Đầu ra (Response): {
"user_id": "string",
"email": "string",
"full_name": "string",
"created_at": "timestamp"
}

Phân quyền: Không yêu cầu xác thực.
Ghi chú: Mật khẩu được băm (bcrypt/Argon2) trước khi lưu.

API-2: Đăng nhập

Mô tả: Xác thực người dùng và trả về JWT.
Phương thức: POST
Endpoint: /api/v1/users/login
Đầu vào (Body): {
"email": "string",
"password": "string"
}

Đầu ra (Response): {
"token": "string",
"user_id": "string",
"email": "string"
}

Phân quyền: Không yêu cầu xác thực.
Ghi chú: JWT chứa user_id, email, và exp (thời gian hết hạn).

API-3: Đăng xuất

Mô tả: Hủy phiên làm việc (xóa JWT phía client).
Phương thức: POST
Endpoint: /api/v1/users/logout
Đầu vào: None (JWT trong header Authorization: Bearer <token>).
Đầu ra (Response): {
"message": "Logged out successfully"
}

Phân quyền: Yêu cầu JWT.

3. API Quản lý Workspace
   API-4: Tạo Workspace

Mô tả: Tạo một Workspace mới, người tạo là Owner.
Phương thức: POST
Endpoint: /api/v1/workspaces
Đầu vào (Body): {
"name": "string"
}

Đầu ra (Response): {
"workspace_id": "string",
"name": "string",
"owner_id": "string",
"created_at": "timestamp"
}

Phân quyền: Yêu cầu JWT.
Ghi chú: Người dùng được thêm vào Workspace_Members với vai trò Owner.

API-5: Lấy danh sách Workspace

Mô tả: Lấy danh sách Workspace mà người dùng là Owner hoặc Member.
Phương thức: GET
Endpoint: /api/v1/workspaces
Đầu vào: None (JWT trong header).
Đầu ra (Response): [
{
"workspace_id": "string",
"name": "string",
"role": "string" // "owner" hoặc "member"
}
]

Phân quyền: Yêu cầu JWT.

API-6: Xóa Workspace

Mô tả: Xóa một Workspace.
Phương thức: DELETE
Endpoint: /api/v1/workspaces/{workspace_id}
Đầu vào: None.
Đầu ra (Response): {
"message": "Workspace deleted successfully"
}

Phân quyền: Yêu cầu JWT, chỉ Owner được phép.
Ghi chú: Kiểm tra vai trò Owner trong Workspace_Members.

4. API Quản lý Thành viên Workspace
   API-7: Tạo link mời

Mô tả: Owner tạo link mời để thêm thành viên vào Workspace.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/invite
Đầu vào: None.
Đgun ra (Response): {
"link_id": "string",
"token": "string",
"expires_at": "timestamp"
}

Phân quyền: Yêu cầu JWT, chỉ Owner được phép.
Ghi chú: token là chuỗi UUID, hết hạn sau 7 ngày, lưu vào Invite_Links.

API-8: Tham gia Workspace qua link

Mô tả: Người dùng tham gia Workspace với vai trò Member qua link mời.
Phương thức: POST
Endpoint: /api/v1/invite/{token}/join
Đầu vào: None (JWT trong header).
Đầu ra (Response): {
"workspace_id": "string",
"name": "string",
"role": "member"
}

Phân quyền: Yêu cầu JWT.
Ghi chú: Kiểm tra token hợp lệ, thêm người dùng vào Workspace_Members với vai trò Member.

API-9: Lấy danh sách thành viên

Mô tả: Lấy danh sách thành viên trong Workspace.
Phương thức: GET
Endpoint: /api/v1/workspaces/{workspace_id}/members
Đầu vào: None.
Đầu ra (Response): [
{
"user_id": "string",
"email": "string",
"full_name": "string",
"role": "string"
}
]

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-10: Xóa thành viên

Mô tả: Xóa một Member khỏi Workspace.
Phương thức: DELETE
Endpoint: /api/v1/workspaces/{workspace_id}/members/{user_id}
Đầu vào: None.
Đầu ra (Response): {
"message": "Member removed successfully"
}

Phân quyền: Yêu cầu JWT, chỉ Owner được phép.

5. API Module Flow (Quản lý Công việc)
   API-11: Lấy danh sách công việc

Mô tả: Lấy danh sách công việc trong Workspace, hỗ trợ lọc theo trạng thái.
Phương thức: GET
Endpoint: /api/v1/workspaces/{workspace_id}/tasks
Đầu vào (Query Params): status (optional, enum: "To Do", "In Progress", "Done").
Đầu ra (Response): [
{
"task_id": "string",
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}
]

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-12: Tạo công việc

Mô tả: Tạo một công việc mới trong Workspace.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/tasks
Đầu vào (Body): {
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Đầu ra (Response): {
"task_id": "string",
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-13: Cập nhật trạng thái công việc

Mô tả: Thay đổi trạng thái công việc (hỗ trợ kéo thả Kanban).
Phương thức: PATCH
Endpoint: /api/v1/workspaces/{workspace_id}/tasks/{task_id}
Đầu vào (Body): {
"status": "string" // "To Do", "In Progress", "Done"
}

Đầu ra (Response): {
"task_id": "string",
"status": "string"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-14: Bật chế độ Pomodoro

Mô tả: Kích hoạt Pomodoro cho một công việc.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/tasks/{task_id}/pomodoro
Đầu vào (Body): {
"duration": number // phút
}

Đầu ra (Response): {
"message": "Pomodoro started"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.
Ghi chú: Backend chỉ lưu trạng thái, logic Pomodoro chạy phía client.

6. API Module Board (Bảng trắng)
   API-15: Tạo bảng trắng

Mô tả: Tạo một bảng trắng trong Workspace.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/boards
Đầu vào (Body): {
"name": "string"
}

Đầu ra (Response): {
"board_id": "string",
"name": "string"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-16: Lấy danh sách bảng trắng

Mô tả: Lấy danh sách bảng trắng trong Workspace.
Phương thức: GET
Endpoint: /api/v1/workspaces/{workspace_id}/boards
Đầu vào: None.
Đầu ra (Response): [
{
"board_id": "string",
"name": "string"
}
]

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-17: Cập nhật đối tượng trên bảng

Mô tả: Thêm/sửa/xóa đối tượng trên bảng trắng (hỗ trợ thời gian thực).
Phương thức: PATCH
Endpoint: /api/v1/workspaces/{workspace_id}/boards/{board_id}/objects
Đầu vào (Body): {
"object_id": "string",
"type": "string", // "sticky_note", "shape", "text"
"position_x": number,
"position_y": number,
"width": number,
"height": number,
"content": {} // JSON tùy thuộc vào type
}

Đầu ra (Response): {
"object_id": "string",
"type": "string",
"position_x": number,
"position_y": number,
"width": number,
"height": number,
"content": {}
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.
Ghi chú: Sử dụng WebSocket (ws://api.brainstormhub.com/v1/workspaces/{workspace_id}/boards/{board_id}/realtime) để đồng bộ thay đổi trong <500ms.

7. API Tích hợp Board và Flow
   API-18: Chuyển đổi ý tưởng

Mô tả: Chuyển một sticky note trên bảng trắng thành công việc trong Flow.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/boards/{board_id}/objects/{object_id}/convert
Đầu vào (Body): {
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Đầu ra (Response): {
"task_id": "string",
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.
Ghi chú: Lấy nội dung từ Board_Objects và tạo bản ghi mới trong Tasks.

8. Yêu cầu Phi chức năng

Bảo mật (NFR-2):
Tất cả API sử dụng HTTPS.
JWT được xác thực qua header Authorization: Bearer <token>.
Kiểm tra vai trò (Owner/Member) trong Workspace_Members trước khi thực thi các hành động nhạy cảm (API-6, API-7, API-10).
Mật khẩu băm bằng bcrypt/Argon2.

Hiệu suất (NFR-1):
Thời gian phản hồi API < 2 giây.
Đồng bộ thời gian thực (API-17) < 500ms.
Sử dụng cache (Redis) cho danh sách Workspace và bảng trắng.

Tính khả dụng (NFR-3):
API tương thích với các trình duyệt hiện đại (Chrome, Firefox, Safari).

Công nghệ (NFR-4):
Backend: Kotlin (Spring Boot/Ktor).
CSDL: PostgreSQL.
WebSocket: Ktor WebSocket hoặc Spring WebSocket.

9. Lưu ý Triển khai

Versioning: Sử dụng /api/v1/ để hỗ trợ nâng cấp.
Error Handling: Trả về mã lỗi chuẩn (400, 401, 403, 404) với thông điệp rõ ràng, ví dụ: {
"error": "Unauthorized: Only Owner can delete workspace"
}

Logging: Ghi log các hành động quan trọng (tạo/xóa Workspace, mời thành viên).
Testing: Viết unit test và integration test cho xác thực/phân quyền.
Ngày cập nhật: 13/07/2025Phiên bản: 1.0Người soạn: Trợ lý ý tưởng

1. Tổng quan
   Danh sách này liệt kê các API backend cần thiết để triển khai các tính năng của Brainstorm Hub, một nền tảng tích hợp brainstorming và quản lý công việc. Các API sử dụng RESTful với JWT cho xác thực/phân quyền, giao tiếp qua HTTPS, và được thiết kế để tương thích với backend Kotlin (Spring Boot/Ktor), cơ sở dữ liệu PostgreSQL, và hỗ trợ cộng tác thời gian thực qua WebSocket.

2. API Quản lý Người dùng (User Management)
   API-1: Đăng ký

Mô tả: Tạo tài khoản người dùng mới.
Phương thức: POST
Endpoint: /api/v1/users/register
Đầu vào (Body): {
"email": "string",
"password": "string",
"full_name": "string"
}

Đầu ra (Response): {
"user_id": "string",
"email": "string",
"full_name": "string",
"created_at": "timestamp"
}

Phân quyền: Không yêu cầu xác thực.
Ghi chú: Mật khẩu được băm (bcrypt/Argon2) trước khi lưu.

API-2: Đăng nhập

Mô tả: Xác thực người dùng và trả về JWT.
Phương thức: POST
Endpoint: /api/v1/users/login
Đầu vào (Body): {
"email": "string",
"password": "string"
}

Đầu ra (Response): {
"token": "string",
"user_id": "string",
"email": "string"
}

Phân quyền: Không yêu cầu xác thực.
Ghi chú: JWT chứa user_id, email, và exp (thời gian hết hạn).

API-3: Đăng xuất

Mô tả: Hủy phiên làm việc (xóa JWT phía client).
Phương thức: POST
Endpoint: /api/v1/users/logout
Đầu vào: None (JWT trong header Authorization: Bearer <token>).
Đầu ra (Response): {
"message": "Logged out successfully"
}

Phân quyền: Yêu cầu JWT.

3. API Quản lý Workspace
   API-4: Tạo Workspace

Mô tả: Tạo một Workspace mới, người tạo là Owner.
Phương thức: POST
Endpoint: /api/v1/workspaces
Đầu vào (Body): {
"name": "string"
}

Đầu ra (Response): {
"workspace_id": "string",
"name": "string",
"owner_id": "string",
"created_at": "timestamp"
}

Phân quyền: Yêu cầu JWT.
Ghi chú: Người dùng được thêm vào Workspace_Members với vai trò Owner.

API-5: Lấy danh sách Workspace

Mô tả: Lấy danh sách Workspace mà người dùng là Owner hoặc Member.
Phương thức: GET
Endpoint: /api/v1/workspaces
Đầu vào: None (JWT trong header).
Đầu ra (Response): [
{
"workspace_id": "string",
"name": "string",
"role": "string" // "owner" hoặc "member"
}
]

Phân quyền: Yêu cầu JWT.

API-6: Xóa Workspace

Mô tả: Xóa một Workspace.
Phương thức: DELETE
Endpoint: /api/v1/workspaces/{workspace_id}
Đầu vào: None.
Đầu ra (Response): {
"message": "Workspace deleted successfully"
}

Phân quyền: Yêu cầu JWT, chỉ Owner được phép.
Ghi chú: Kiểm tra vai trò Owner trong Workspace_Members.

4. API Quản lý Thành viên Workspace
   API-7: Tạo link mời

Mô tả: Owner tạo link mời để thêm thành viên vào Workspace.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/invite
Đầu vào: None.
Đgun ra (Response): {
"link_id": "string",
"token": "string",
"expires_at": "timestamp"
}

Phân quyền: Yêu cầu JWT, chỉ Owner được phép.
Ghi chú: token là chuỗi UUID, hết hạn sau 7 ngày, lưu vào Invite_Links.

API-8: Tham gia Workspace qua link

Mô tả: Người dùng tham gia Workspace với vai trò Member qua link mời.
Phương thức: POST
Endpoint: /api/v1/invite/{token}/join
Đầu vào: None (JWT trong header).
Đầu ra (Response): {
"workspace_id": "string",
"name": "string",
"role": "member"
}

Phân quyền: Yêu cầu JWT.
Ghi chú: Kiểm tra token hợp lệ, thêm người dùng vào Workspace_Members với vai trò Member.

API-9: Lấy danh sách thành viên

Mô tả: Lấy danh sách thành viên trong Workspace.
Phương thức: GET
Endpoint: /api/v1/workspaces/{workspace_id}/members
Đầu vào: None.
Đầu ra (Response): [
{
"user_id": "string",
"email": "string",
"full_name": "string",
"role": "string"
}
]

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-10: Xóa thành viên

Mô tả: Xóa một Member khỏi Workspace.
Phương thức: DELETE
Endpoint: /api/v1/workspaces/{workspace_id}/members/{user_id}
Đầu vào: None.
Đầu ra (Response): {
"message": "Member removed successfully"
}

Phân quyền: Yêu cầu JWT, chỉ Owner được phép.

5. API Module Flow (Quản lý Công việc)
   API-11: Lấy danh sách công việc

Mô tả: Lấy danh sách công việc trong Workspace, hỗ trợ lọc theo trạng thái.
Phương thức: GET
Endpoint: /api/v1/workspaces/{workspace_id}/tasks
Đầu vào (Query Params): status (optional, enum: "To Do", "In Progress", "Done").
Đầu ra (Response): [
{
"task_id": "string",
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}
]

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-12: Tạo công việc

Mô tả: Tạo một công việc mới trong Workspace.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/tasks
Đầu vào (Body): {
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Đầu ra (Response): {
"task_id": "string",
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-13: Cập nhật trạng thái công việc

Mô tả: Thay đổi trạng thái công việc (hỗ trợ kéo thả Kanban).
Phương thức: PATCH
Endpoint: /api/v1/workspaces/{workspace_id}/tasks/{task_id}
Đầu vào (Body): {
"status": "string" // "To Do", "In Progress", "Done"
}

Đầu ra (Response): {
"task_id": "string",
"status": "string"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-14: Bật chế độ Pomodoro

Mô tả: Kích hoạt Pomodoro cho một công việc.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/tasks/{task_id}/pomodoro
Đầu vào (Body): {
"duration": number // phút
}

Đầu ra (Response): {
"message": "Pomodoro started"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.
Ghi chú: Backend chỉ lưu trạng thái, logic Pomodoro chạy phía client.

6. API Module Board (Bảng trắng)
   API-15: Tạo bảng trắng

Mô tả: Tạo một bảng trắng trong Workspace.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/boards
Đầu vào (Body): {
"name": "string"
}

Đầu ra (Response): {
"board_id": "string",
"name": "string"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-16: Lấy danh sách bảng trắng

Mô tả: Lấy danh sách bảng trắng trong Workspace.
Phương thức: GET
Endpoint: /api/v1/workspaces/{workspace_id}/boards
Đầu vào: None.
Đầu ra (Response): [
{
"board_id": "string",
"name": "string"
}
]

Phân quyền: Yêu cầu JWT, Owner hoặc Member.

API-17: Cập nhật đối tượng trên bảng

Mô tả: Thêm/sửa/xóa đối tượng trên bảng trắng (hỗ trợ thời gian thực).
Phương thức: PATCH
Endpoint: /api/v1/workspaces/{workspace_id}/boards/{board_id}/objects
Đầu vào (Body): {
"object_id": "string",
"type": "string", // "sticky_note", "shape", "text"
"position_x": number,
"position_y": number,
"width": number,
"height": number,
"content": {} // JSON tùy thuộc vào type
}

Đầu ra (Response): {
"object_id": "string",
"type": "string",
"position_x": number,
"position_y": number,
"width": number,
"height": number,
"content": {}
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.
Ghi chú: Sử dụng WebSocket (ws://api.brainstormhub.com/v1/workspaces/{workspace_id}/boards/{board_id}/realtime) để đồng bộ thay đổi trong <500ms.

7. API Tích hợp Board và Flow
   API-18: Chuyển đổi ý tưởng

Mô tả: Chuyển một sticky note trên bảng trắng thành công việc trong Flow.
Phương thức: POST
Endpoint: /api/v1/workspaces/{workspace_id}/boards/{board_id}/objects/{object_id}/convert
Đầu vào (Body): {
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Đầu ra (Response): {
"task_id": "string",
"title": "string",
"description": "string",
"status": "string",
"assignee_id": "string",
"due_date": "timestamp"
}

Phân quyền: Yêu cầu JWT, Owner hoặc Member.
Ghi chú: Lấy nội dung từ Board_Objects và tạo bản ghi mới trong Tasks.

8. Yêu cầu Phi chức năng

Bảo mật (NFR-2):
Tất cả API sử dụng HTTPS.
JWT được xác thực qua header Authorization: Bearer <token>.
Kiểm tra vai trò (Owner/Member) trong Workspace_Members trước khi thực thi các hành động nhạy cảm (API-6, API-7, API-10).
Mật khẩu băm bằng bcrypt/Argon2.

Hiệu suất (NFR-1):
Thời gian phản hồi API < 2 giây.
Đồng bộ thời gian thực (API-17) < 500ms.
Sử dụng cache (Redis) cho danh sách Workspace và bảng trắng.

Tính khả dụng (NFR-3):
API tương thích với các trình duyệt hiện đại (Chrome, Firefox, Safari).

Công nghệ (NFR-4):
Backend: Kotlin (Spring Boot/Ktor).
CSDL: PostgreSQL.
WebSocket: Ktor WebSocket hoặc Spring WebSocket.

9. Lưu ý Triển khai

Versioning: Sử dụng /api/v1/ để hỗ trợ nâng cấp.
Error Handling: Trả về mã lỗi chuẩn (400, 401, 403, 404) với thông điệp rõ ràng, ví dụ: {
"error": "Unauthorized: Only Owner can delete workspace"
}

Logging: Ghi log các hành động quan trọng (tạo/xóa Workspace, mời thành viên).
Testing: Viết unit test và integration test cho xác thực/phân quyền.
