var renderServer = function (comments) {
    var data = Java.from(comments);
    return React.renderToString(
        <CommentBox data={data} url='comments.json' pollInterval={5000} />
    );
};