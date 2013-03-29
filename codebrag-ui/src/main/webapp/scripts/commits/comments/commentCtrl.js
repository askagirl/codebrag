'use strict';

angular.module('codebrag.commits.comments')

    .controller('CommentCtrl',function ($scope, currentCommit, Comments) {

        $scope.commentsList = [];

        loadAllCommentsForCurrentCommit();

        function loadAllCommentsForCurrentCommit() {
            if(currentCommit.isSelected()) {
                Comments.query({id: currentCommit.id}, function(data) {
                    $scope.commentsList = data.comments;
                })
            }
        }

        $scope.addComment = {
            commitId: currentCommit.id,
            body: ''
        };

        $scope.submitComment = function () {
            Comments.save($scope.addComment, function (data) {
                // TODO: replace this with full comment data returned from server when done
                var addedComment = {
                    id: data,
                    authorName: 'mostr',
                    message: $scope.addComment.body,
                    time: new Date().toString()
                };
                $scope.commentsList.push(addedComment);
            })
        }

    })

