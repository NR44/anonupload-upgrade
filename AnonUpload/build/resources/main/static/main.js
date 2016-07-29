function getFiles(filesData) {
    for (var i in filesData) {
        var elem = $("<a>");
        var delelem = $("<button>");

        elem.attr("href", "files/" + filesData[i].filename);
        // delelem.attr("href", "/delete" );
        // delelem.attr("name", "fileId");
        // delelem.attr("value", filesData[i].Id);
        // delelem.text("Delete");

        var newId = filesData[i].originalFilename;

        if (filesData[i].comment == null) {
            elem.text(filesData[i].originalFilename);
        } else {
            elem.text(filesData[i].comment);
        }

        $("#fileList").append(elem);
        // $("#fileList").append(delelem);
        var lineBreak = $('<form action="/delete" method="post">\
        <input type="password" placeholder="Enter password" name="password"/>\
        <input id="fileName" name="deleteFile" type="text" hidden/>\
            <button id="deleteButton" type="submit">Delete</button>\
            </form><br/>');

        $("#fileList").append(lineBreak);
        $("input[id = fileName]").val(newId);
    }
}

$.get("/files", getFiles);