if(uiLabelMap === undefined)
	var _tmpMsg = {
		"vi" : "",	
		"en" : ""
 	}
	
	var uiLabelMap = {
			day_of_week  : {
					'MONDAY' : {
						"vi" : "Thứ 2",
						"en" : "Monday",
						"alias" : "T2"
					},
					'TUESDAY' : {
						"vi" : "Thứ 3",
						"en" : "Tuesday",
						"alias" : "T3"
					},
					'WEDNESDAY' : {
						"vi" : "Thứ 4",
						"en" : "Wednesday",
						"alias" : "T4"
					},
					'THURSDAY' : {
						"vi" : "Thứ 5",
						"en" : "Thursday",
						"alias" : "T5"
					},
					'FRIDAY' : {
						"vi" : "Thứ 6",
						"en" : "Friday",
						"alias" : "T6"
					},
					'SATURDAY' : {
						"vi" : "Thứ 7",
						"en" : "Saturday",
						"alias" : "T7"
					},
					'SUNDAY' : {
						"vi" : "Chủ nhật",
						"en" : "Sunday",
						"alias" : "CN"
					}
			},
			month_of_year : {
				'1' : {
					'vi' : 'Tháng 1',
					'en' : 'January'
				},
				'2' : {
					'vi' : 'Tháng 2',
					'en' : 'February'
				},
				'3' : {
					'vi' : 'Tháng 3',
					'en' : 'March'
				},
				'4' : {
					'vi' : 'Tháng 4',
					'en' : 'April'
				},
				'5' : {
					'vi' : 'Tháng 5',
					'en' : 'May'
				},
				'6' : {
					'vi' : 'Tháng 6',
					'en' : 'June'
				},
				'7' : {
					'vi' : 'Tháng 7',
					'en' : 'July'
				},
				'8' : {
					'vi' : 'Tháng 8',
					'en' : 'August'
				},
				'9' : {
					'vi' : 'Tháng 9',
					'en' : 'September'
				},
				'10' : {
					'vi' : 'Tháng 10',
					'en' : 'October'
				},
				'11' : {
					'vi' : 'Tháng 11',
					'en' : 'November'
				},
				'12' : {
					'vi' : 'Tháng 12',
					'en' : 'December'
				}
			},
			'username' : {
							'vi' : 'Tên đăng nhập',
							'en' : 'Username'
						},
			'password' : {
				'vi' : 'Mật khẩu',
				'en' : 'Password'
			},
			"Login":{
				"vi" : "Đăng nhập",
				"en" : "Login"
			},
			"SignUp": {
				"vi" : "Đăng ký",
				"en" : "Sign up"
			},
			"Profile": {
				"vi" : "Thông tin cá nhân",
				"en" : "Profile"
			},
			"SalesmanList" : {
				"vi" : "Danh sách NVBH",
				"en" : "Saler list"
			},
			"Leave" : {
				"vi" : "Xin nghỉ phép",
				"en" : "Leave"
			},
			"Dashboard" : {
				"vi" : "Thống kê",
				"en" : "Dashboard"
			},
			"Logout" : {
				"vi" : "Đăng xuất",
				"en" : "Log out"
			},
			"RouteList" : {
				"vi" : "Danh sách tuyến",
				"en" : "Route list"
			},
			"LogoutNtf" : {
				"vi" : "Bạn có chắc muốn đăng xuất khỏi ứng dụng?",
				"en" : "Are you sure logout app?"
			},
			"LogInTimeout" : {
				"vi" : "Thời gian đăng nhập đã hết,hãy đăng nhập lại",
				"en" : "Time login ended,please login app again"
			},
			"del_success" : {
				"vi" : "Xóa tuyến thành công",
				"en" : "Delete Route successfully"
			},
			"delConfirm" : {
				"vi" : "Ban có chắc muốn xóa tuyến " + _tmpMsg["vi"],
				"en" : "Are you sure delete route " + _tmpMsg["en"]
			},
			"RouteDetail" : {
				"vi" : "Thông tin chi tiết",
				"en" : "The Route Detail"
			},
			"InforDetail" : {
				"vi" : "Thông tin chi tiết",
				"en" : "The information detail"
			},
			"routeId" : {
				"vi" : "Mã tuyến",
				"en" : "Route ID"
			},
			"route_name" : {
				"vi" : "Tên tuyến",
				"en" : "Route Name"
			},
			"saler" : {
				"vi" : "Nhân viên bán hàng",
				"en" : "Saler"
			},
			"titleConfirm" : {
				"vi" : "Mẹo",
				"en" : "Tip"
			},
			"description" : {
				"vi" : "Mô tả",
				"en" : "Description"
			},
			"routeSchedule" : {
				"vi" : "Thời gian tuyến",
				"en" : "Route schedule"
			},
			"add_route" : {
				"vi" : "Thêm tuyến",
				"en" : "Add new route"
			},
			"addRouteNotSuccess" : {
				"vi" : "Thêm tuyến không thành công",
				"en" : "Add new route unsuccessful"
			},
			"UpdateAddress" : {
				"vi" : "Cập nhật địa chỉ",
				"en" : "Update Address"
			},
			"UpdateAddressSuccess" : {
				"vi" : "Cập nhật địa chỉ thành công",
				"en" : "Update Address successfully"
			},
			"UpdateAddressFailed" : {
				"vi" : "Cập nhật địa chỉ không thành công",
				"en" : "Update Address failed"
			},
			"enterAddress" : {
				"vi" : "Nhập địa chỉ mới...",
				"en" : "Enter new address..."
			},
			"LocationNotInScope" : {
				"vi" : "Địa chỉ nằm ngoài vùng,hãy chọn lại vị trí khác",
				"en" : "This location is not valid,please choose other locations"
			},
			"Exhibition" : {
				"vi" : "Chương trình trưng bày",
				"en" : "The Exhibition"
			},
			"Accumulation" : {
				"vi" : "Chương trình tích lũy",
				"en" : "The Accumulation"
			},
			"EmployeeDetail" : {
				"vi" : "Chi tiết nhân viên",
				"en" : "Employee Detail"
			},
			"employeeName" : {
				"vi" : "Tên nhân viên",
				"en" : "Employee Name"
			},
			"employeeId" : {
				"vi" : "Mã nhân viên",
				"en" : "Employee Id"
			},
			"sex" : {
				"vi" : "Giới tính",
				"en" : "Sex"
			},
			"birthDay" : {
				"vi" : "Ngày sinh",
				"en" : "Birthday"
			},
			"phoneNumber" : {
				"vi" : "Số điện thoại",
				"en" : "Phone number"
			},
			"saleSupervisor" : {
				"vi" : "Giám sát bán hàng",
				"en" : "Sale Supervisor"
			},
			"ProgramExhAcc" : {
				"vi" : "Chương trình trưng bày/tích lũy",
				"en" : "Program Exhibition/Accumulation"
			},
			"ProgramExhAccLabel" : {
				"vi" : "Trưng bày / tích lũy",
				"en" : "Exhibition/Accumulation"
			},
			"ProgramExhAccDetail" : {
				"vi" : "Chi tiết chương trình trưng bày/tích lũy",
				"en" : "Program Exhibition/Accumulation Detail"
			},
			"programCode" : {
				"vi" : "Mã CTKM",	
				"en" : "Program Code"
			},
			"programName" : {
				"vi" : "Tên CTKM",	
				"en" : "Program Name"
			},
			"status" : {
				"vi" : "Trạng thái",
				"en" : "status"
			},
			"content" : {
				"vi" : "Nội dung",
				"en" : "Content"
			},
			"startDate" : {
				"vi" : "Ngày bắt đầu",
				"en" : "Start date"
			},
			"endDate" : {
				"vi" : "Ngày kết thúc",
				"en" : "End date"
			},
			"fromDate" : {
				"vi" : "Từ Ngày",
				"en" : "From Date"
			},
			"thruDate" : {
				"vi" : "Đến Ngày",
				"en" : "Thru Date"
			},
			"store" : {
				"vi" : "Cửa hàng",
				"en" : "Store"
			},
			"ObjectApply" : {
				"vi" : "Đối tượng áp dụng",
				"en" : "Subject of applications"
			},
			"RequireVoucher" : {
				"vi" : "Yêu cầu mã Voucher",
				"en" : "Vourcher id Required"
			},
			"ruleName" : {
				"vi" : "Tên luật - Mô tả",
				"en" : "Rule Name - Description"
			},
			"condition" : {
				"vi" : "Điều kiện",
				"en" : "Condition"
			},
			"CategoryAppl" : {
				"vi" : "Danh mục/Sản phẩm Áp dụng",
				"en" : "Category/Products Application"
			},
			"Policy" : {
				"vi" : "Chính sách",
				"en" : "Policy"
			},
			"Notice" : {
				"vi" : "Chú ý",
				"en" : "Notice"
			},
			"Approved" : {
				"vi" : "Phê duyệt chương trình KM",
				"en" : "Approved"
			},
			"ApprovedConfirm" : {
				"vi" : "Bạn có chắc muốn phê duyệt chương trình KM này không?",
				"en" : "Are you sure approved this promotions ? "
			},
			"NotPromoValid" : {
				"vi" : "Không tồn tại chương trình KM,hãy kiểm tra lại",
				"en" : "Approved not valid"
			},
			"ApprovedSuccess" : {
				"vi" : "Phê duyệt chương trình KM thành công",
				"en" : "Approved successfully"
			},
			"ApprovedFailed" : {
				"vi" : "Phê duyệt chương trình KM không thành công",
				"en" : "Approved failed"
			},
			"ApproveNotify" : {
				"vi" : "Chỉ cập nhật được các chương trình khuyến mại có trạng thái là 'mới tạo'",
				"en" : "Just update promotions have status is new created"
			},
			"ChartEmplByTurnOver" : {
				"vi" : "Biểu đồ xếp hạng nhân viên bán hàng theo doanh số",
				"en" : "Chart sales staff ranked according to Turnover"
			},
			"customerRankchart" : {
				"vi" : "Biểu đồ xếp hạng khách hàng",
				"en" : "Customer rating charts"
			},
			"noResultFound" : {
				"vi" : "Không còn dữ liệu hiển thị",
				"en" : "No result found for display" 
			},
			"filter" : {
				"en"  : "Filter by",
				"vi" : "Lọc theo"
			},
			"otherList" : {
				"en"  : "Other",
				"vi" : "Khác"
			},
			"Roads" : {
				"en"  : "Roads",
				"vi" : "Tuyến đường"
			},
			"Programs" : {
				"en"  : "Programs",
				"vi" : "Các chương trình"
			},
			"NotValidAddress" : {
				"en"  : "Please choose location to update address",
				"vi" : "Hãy chọn địa chỉ cần cập nhật"
			},
			"ConfirmUpdateRoute" : {
				"en"  : "Are you sure update with this address?",
				"vi" : "Bạn có chắc muốn cập nhật địa chỉ hiện tại?"
			},
			"search" : {
				"en" : "Search...",
				"vi" : "Tìm kiếm..."
			},
			"isNoResultFound" : {
				"en" : "No result found",
				"vi" : "Không có dữ liệu"
			},
			"CheckNetWorkDevice" : {
				"en" : "No wifi available,please check network again",
				"vi" : "Không có kết nối mạng,xin hãy kiểm tra lại"
			},
			"storeList" : {
				"en" : "Stores List",
				"vi" : "Danh sách đại lý"
			},
			"StoreDetail" : {
				"en" : "Store Detail",
				"vi" : "Chi tiết đại lý"
			},
			"LeaveDetail" : {
				"en" : "Leave Detail",
				"vi" : "Chi tiết nghỉ phép"
			},
			"StoreCode" : {
				"en" : "Store Code",
				"vi" : "Mã đại lý"
			},
			"StoreName" : {
				"en" : "Store Name",
				"vi" : "Tên đại lý"
			},
			"Distributor" : {
				"en" : "Distributor",
				"vi" : "Nhà phân phối"
			},
			"Email" : {
				"en" : "Email",
				"vi" : "Email"
			},
			"All" : {
				"en" : "All",
				"vi" : "Tất cả"
			},
			"toDay" : {
				"en" : "Today",
				"vi" : "Hôm nay"
			},
 			"Address"  : {
				"en" : "Address",
				"vi" : "Địa chỉ"
			},
			"statusStore" : {
					"enabled" : {
						"en" : "Active",
						"vi" : "Đang hoạt động"
					},
					"disabled" : {
						"en" : "Not Working",
						"vi" : "Không hoạt động"
					}
			},
			"NotValid" : {
				"en" : "Not Valid",
				"vi" : "Chưa có"
			},
			"Cancel" : {
				"en" : "Cancel",
				"vi" : "Thôi"
			},
			"InventoryInformartion" : {
				"en" : "Inventory Information",
				"vi" : "Thông tin kiểm tồn"
			},
			"productId" : {
				"en" : "productId",
				"vi" : "Mã sản phẩm"
			},
			"product" : {
				"en" : "Product",
				"vi" : "Sản phẩm"
			},
			"quantity" : {
				"en" : "Quantity",
				"vi" : "Số lượng"
			},
			"timeInventory" : {
				"en" : "Time check inventory",
				"vi":"Thời gian kiểm kho"
			},
			"InventoryList" : {
				"en" : "Inventory List",
				"vi" : "Danh sách hàng tồn"
			},
			"InforCommon" : {
				"en" : "Information Common",
				"vi" : "Thông tin chung"
			},
			"OtherInfor" : {
				"en" : "Other Information",
				"vi" : "Thông tin khác"
			},
			"Position" : {
				"en" : "Position",
				"vi" : "Vị trí"
			},
			"LoginName" : {
				"en" : "Login name",
				"vi" : "Tên đăng nhập"
			},
			"numCMND" : {
				"en" : "",
				"vi" : "Số CMND"
			},
			"IssuePlace" : {
				"en" : "Place of issue",
				"vi" : "Nơi cấp"
			},
			"IssueDate" : {
				"en" : "Date of issue",
				"vi" : "Ngày cấp"
			},
			"NativeLand" : {
				"en" : "Native land",
				"vi" : "Quê quán"
			},
			"Ethnic" : {
				"en" : "Ethnic",
				"vi" : "Dân tộc"
			},
			"Religion" : {
				"en" : "Religion",
				"vi" : "Tôn giáo"
			},
			"Nationality" : {
				"en" : "Nationality",
				"vi" : "Quốc tịch"
			},
			"MarialStatus" : {
				"en" : "Marial Status",
				"vi" : "Tình trạng hôn nhân"
			},
			"CurrentPosition" : {
				"en" : "Current Position",
				"vi" : "Vị trí hiện tại"
			},
			"Department" : {
				"en" : "Department",
				"vi" : "Phòng ban"
			},
			"ContactInfor" : {
				"en" : "Contact Infor",
				"vi" : "Thông tin liên lạc"
			},
			"PermanentResidence" : {
				"en" : "Permanent Residence",
				"vi" : "Hộ khẩu thường trú"
			},
			"AddressContact" : {
				"en" : "Contact Address",
				"vi" : "Địa chỉ liên hệ"
			},
			"MobilePhone" : {
				"en" : "Mobile Phone",
				"vi" : "Điện thoại di động"
			},
			"BaseInfor" : {
				"en" : "Base information",
				"vi" : "Thông tin cơ bản"
			},
			"ContactInfor" : {
				"en" : "Contact Information",
				"vi" : "Thông tin liên lạc"
			},
			"leaveReason" : {
				"en" : "Leave Reason",
				"vi" : "Lý do nghỉ"
			},
			"accept" : {
				"en" : "Accept",
				"vi" : "Chấp nhận"
			},
			"reject" : {
				"en" : "Reject",
				"vi" : "Từ chối"
			},
			"cancelLeave" : {
				"en" : "Cancel",
				"vi" : "Hủy đơn"
			},
			"noteApprove" : {
				"en" : "Approve Note",
				"vi" : "Ghi chú phê duyệt"
			},
			"ApprovedSuccess" : {
				"en" : "Approve Success",
				"vi" : "Phê duyệt thành công"
			},
			"ApprovedFailed" : {
				"en" : "Approve Failed",
				"vi" : "Phê duyệt không thành công"
			},
			"ApproveLeaveConfirm" : {
				"en" : "Are you sure approve leave with this action?",
				"vi" : "Bạn có chắc muốn duyệt đơn nghỉ phép?"
			}
	}