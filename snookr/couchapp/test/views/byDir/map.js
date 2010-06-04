function(doc) {
    var dirs=doc.fileName.split("/");
    dirs = dirs.slice(1,-1);
    emit(dirs, 1);
    /*dirs = dirs.slice(0,-1);
    emit(dirs, 1);
    dirs = dirs.slice(0,-1);
    emit(dirs, 1);
    dirs = dirs.slice(0,-1);
    emit(dirs, 1); */
}