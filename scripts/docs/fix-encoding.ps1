param(
    [Parameter(Mandatory=$true)]
    [string]$FilePath
)

# 设置输出编码为UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

try {
    # 检查文件是否存在
    if (-not (Test-Path $FilePath)) {
        Write-Error "文件不存在: $FilePath"
        exit 1
    }

    # 读取文件内容（使用默认编码读取原始字节）
    $bytes = [System.IO.File]::ReadAllBytes($FilePath)
    
    # 尝试检测编码
    $encoding = $null
    
    # 检查BOM标记
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $encoding = [System.Text.Encoding]::UTF8
    } elseif ($bytes.Length -ge 2 -and $bytes[0] -eq 0xFF -and $bytes[1] -eq 0xFE) {
        $encoding = [System.Text.Encoding]::Unicode
    } elseif ($bytes.Length -ge 2 -and $bytes[0] -eq 0xFE -and $bytes[1] -eq 0xFF) {
        $encoding = [System.Text.Encoding]::BigEndianUnicode
    } else {
        # 没有BOM，使用系统默认编码
        $encoding = [System.Text.Encoding]::Default
    }
    
    # 使用检测到的编码读取内容
    $content = $encoding.GetString($bytes)
    
    # 检查是否已经有UTF-8 meta标签
    if ($content -match '<meta\s+charset=["\']UTF-8["\']\s*/?>') {
        # 已经有UTF-8声明，只需要重新以UTF-8保存
        [System.IO.File]::WriteAllText($FilePath, $content, [System.Text.Encoding]::UTF8)
        exit 0
    }
    
    # 添加UTF-8 meta标签到head部分
    if ($content -match '<head>') {
        $newContent = $content -replace '<head>', '<head>' + "`r`n    <meta charset=`"UTF-8`">"
        [System.IO.File]::WriteAllText($FilePath, $newContent, [System.Text.Encoding]::UTF8)
        exit 0
    } else {
        Write-Error "HTML文件中没有找到<head>标签"
        exit 1
    }
} catch {
    Write-Error "处理文件时出错: $_"
    exit 1
}
