Add-Type -AssemblyName System.Drawing
$imagePath = "app\src\main\res\drawable\app_icon.jpg"
$outPath = "app\src\main\res\drawable\app_icon_round.png"

$img = [System.Drawing.Image]::FromFile($imagePath)
$size = [math]::Min($img.Width, $img.Height)

$bmp = New-Object System.Drawing.Bitmap $size, $size
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

# Prevent background
$g.Clear([System.Drawing.Color]::Transparent)

# Create a circle path
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.AddEllipse(0, 0, $size, $size)
$g.SetClip($path)

# Draw image centered
$offsetX = -($img.Width - $size)/2
$offsetY = -($img.Height - $size)/2
$g.DrawImage($img, $offsetX, $offsetY)

$bmp.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)

$g.Dispose()
$bmp.Dispose()
$img.Dispose()
