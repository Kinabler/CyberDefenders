# Đường dẫn file hex đầu vào và file nhị phân đầu ra
$inputPath = "$PSScriptRoot\output.txt"       # File hex đầu vào (output.txt từ script trước)
$outputPath = "$PSScriptRoot\restored_binary.bin" # File nhị phân đầu ra

# Kiểm tra nếu file hex tồn tại
if (-Not (Test-Path -Path $inputPath)) {
    Write-Host "File not found: $inputPath"
    exit
}

# Mở stream đọc file hex và ghi vào file nhị phân
$reader = [System.IO.StreamReader]::new($inputPath)
$writer = [System.IO.File]::OpenWrite($outputPath)

# Đọc từng dòng (mỗi dòng là 100KB dữ liệu hex) và chuyển đổi thành nhị phân
while ($null -ne ($hexString = $reader.ReadLine())) {
    # Kiểm tra độ dài chuỗi hex
    if ($hexString.Length % 2 -ne 0) {
        Write-Host "Invalid hex data: Hex string length must be even."
        exit
    }

    # Tạo mảng byte cho chunk hiện tại
    $byteArray = [byte[]]::new($hexString.Length / 2)

    # Chuyển từng cặp ký tự hex thành byte
    for ($i = 0; $i -lt $hexString.Length; $i += 2) {
        $byteArray[$i / 2] = [Convert]::ToByte($hexString.Substring($i, 2), 16)
    }

    # Ghi mảng byte vào file nhị phân đầu ra
    $writer.Write($byteArray, 0, $byteArray.Length)
}

# Đóng file streams
$reader.Close()
$writer.Close()
Write-Host "Binary data has been restored and saved to $outputPath."
