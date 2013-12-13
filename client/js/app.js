// load the application when DOM is ready (using jQuery)
$(function() {
	"use strict";

	var debug = true;

	// some "advanced" log output
	var logger = function(output, type) {
		if (debug) {
			var date = new Date();
			var h = date.getHours();
			var m = date.getMinutes();
			var s = date.getSeconds();

			if (h < 10)	h = '0'+h;
			if (m < 10)	m = '0'+m;
			if (s < 10)	s = '0'+s;

			if (type === '' || type === 'log') console.log(h+':'+m+':'+s+' '+output);
			else if (type === 'info') console.info(h+':'+m+':'+s+' '+output);
			else if (type === 'error') console.error(h+':'+m+':'+s+' '+output);
		}
	}

	// when an ajax request starts, show spinner animation
    $(this).ajaxStart(function(){
        $('.loading-overlay').fadeIn(150);
    });

    // when an ajax request completes, hide spinner animation  
    $(this).ajaxStop(function(){
        $('.loading-overlay').fadeOut(700);
    });

	// module pattern, app namespace
	var feder = (function() {

		var _this; // blame javascript scopes

		// webservice root (no trailing /)
		var _urlRoot = 'http://localhost:8080/federwebservice/rest';

		// current post and user references
		var currentUser, currentPost;

		/*
		 * User Model
		 * model to represent the signed in user
		 */
		var User = Backbone.Model.extend({
			urlRoot: _urlRoot+'/users',
			defaults: {
				id: 0,
				username: '',
				email: ''
			},
			initialize: function() {
				// logger('[User Instance Created] '+JSON.stringify(this.toJSON()), 'info');
			}
		});

		/*
		 * Post Model
		 * model to represent single posts
		 */
		var Post = Backbone.Model.extend({
			urlRoot: _urlRoot+'/posts',
	        defaults: {
	        	authorId: '',
	            title: '',
	            subtitle: '',
	            body: '',
	            image: '',
	            creationDate: '',
	            collectionId: -1,
	            isDraft: 1,
	            views: 0
	        },
	        initialize: function() {
	        	// logger('[Post Instance Created] '+JSON.stringify(this.toJSON()), 'info');
	        }
	    });

	    /*
		 * Comment Model
		 * model to represent single comments
		 */
		var Comment = Backbone.Model.extend({
			urlRoot: _urlRoot+'/comments',
	        defaults: {
	        	authorId: '',
	        	postId: '',
	            body: '',
	            creationDate: ''
	        },
	        initialize: function() {
	        	// logger('[Comment Instance Created] '+JSON.stringify(this.toJSON()), 'info');
	        }
	    });

		/*
		 * Collection Model
		 * model to represent single collections of posts
		 * not to be confused with the Backbone.js collection type
		 */
	    var Collection = Backbone.Model.extend({
	    	urlRoot: _urlRoot+'/collections',
	    	defaults: {
	    		title: '',
	    		image: '',
	    		views: 0
	    	},
	    	initialize: function() {
	    		// logger('[Collection Instance Created] '+JSON.stringify(this.toJSON()), 'info');
	    	}
	    });

	    /*
		 * Collection Collection ;P
		 * list of post collections from the server
		 */
	    var Collections = Backbone.Collection.extend({
			model: Collection,
			url: _urlRoot+'/collections'
		});

		/*
		 * Post Collection
		 * backbone collection of the current user's posts
		 */
	    var UserPosts = Backbone.Collection.extend({
			model: Post,
			url: _urlRoot+'/users/1/posts'	// TODO: set real user id
		});

		/*
		 * SidebarView
		 * the view for our sidebar
		 */
		var SidebarView = Backbone.View.extend({
			el: $('.sidebar'),
			initialize: function() {

			},
			loadViewState: function(state) {
				switch(state) {
					case 'homepage':
						var markup = $('#homepage-sidebar').clone();
						$('.sidebar').empty().append(markup);
						break;
					case 'post':
						var markup = $('#post-details-sidebar').clone();
						$('.sidebar').empty().append(markup);
						break;
					case 'post-editing':
						var markup = $('#post-editing-sidebar').clone();
						$('.sidebar').empty().append(markup);
						break;
				}
			}
		});

		/*
		 * ContentView
		 * the view for our sidebar
		 */
		var ContentView = Backbone.View.extend({
			el: $('.content'),
			initialize: function() {

			},
			loadViewState: function(state) {
				switch(state) {
					case 'homepage':
						var markup = $('#homepage-content').clone();
						$('.content').empty().append(markup);
						break;
					case 'login':
						var markup = $('#login-content').clone();
						$('.content').empty().append(markup);
						break;
					case 'manage':
						var markup = $('#manage-posts-content').clone();
						$('.content').empty().append(markup);
						break;
					case 'post':
						var markup = $('#post-content').clone();
						$('.content').empty().append(markup);
						break;
					case 'post-editing':
						var markup = $('#post-editing-content').clone();
						$('.content').empty().append(markup);
						break;
				}
			}
		});

		/*
		 * AppView
		 * the main view for our application
		 */
		var AppView = Backbone.View.extend({
			el: $('body'),
			events: {
				'click .home-button': 			'showHomeView',
				'click .new-post-button':		'showEditorView',
				'click .manage-posts-button':	'showManageView',
				'click .save-post':				'saveCurrentPost'
			},
			initialize: function() {
				logger('Feder App started.', 'info');

				_this = this; // global reference to this. again, blame javascript scopes

				// event delegation (dynamic binding)
				this.delegateEvents({ 
					'click .home-button': 			'showHomeView',
					'click .new-post-button':		'showEditorView',
					'click .manage-posts-button':	'showManageView',
					'click .save-post':				'saveCurrentPost'
				});

				// load start views
				this.sidebarView = new SidebarView();
				this.sidebarView.loadViewState('homepage');

				this.contentView = new ContentView();
				this.contentView.loadViewState('homepage');

				// sample user instantiation (signed in user)
				this.currentUser = new User({ id: 1, username: 'Philipp' });

				// user's own post collection
				this.userPosts = new UserPosts();

				// instantiate and fetch list of post collections from server
				this.collections = new Collections();
				this.collections.fetch();

				// sort collections list by views (descending)
				this.collections.models = _.sortBy(this.collections.models, function(o) { return o.get('views'); });
				this.collections.models.reverse();

				// set up medium.js plugin
				this.setupMedium();

				// set up grande toolbar plugin
				grande.bind(document.getElementById('post-body'));

				// set up modal plugin
	            $('.modal-link').click(function(event) {
					event.preventDefault();
					$(this).modal({
						fadeDuration: 150
					});
				});
			},
			showHomeView: function() {
				logger('[View Changed] Home View', 'info');

				this.contentView.loadViewState('homepage');
				this.sidebarView.loadViewState('homepage');
			},
			showCollectionView: function() {
				logger('[View Changed] Collection View', 'info');
			},
			showCollectionDetailView: function() {
				logger('[View Changed] Collection Detail View', 'info');
			},
			showEditorView: function(event, post) {
				logger('[View Changed] Editor View', 'info');

				// editing existing post? -> fill form fields with post data...
				if (post) {
					$('.post-body').empty().append(post.get('body'));
					$('.post-title').empty().append('<p>'+post.get('title')+'</p>');
					$('.post-subtitle').empty().append(post.get('subtitle'));
					this.currentPost = post;

				// ...else start with an empty post
				} else {
					$('.post-body, .post-title, .post-subtitle').empty();
				}

				this.sidebarView.loadViewState('post-editing');
				this.contentView.loadViewState('post-editing');
				this.setupMedium();
			},
			showManageView: function() {
				logger('[View Changed] Manage View', 'info');

				// fetch all posts from user
				this.userPosts.fetch({
					success: function(collection, response, options) {
						// empty previous post list
						$('.post-list').empty();

						// generate markup
						_.each(collection.models, function(value, key, list) {
							value.set({ id: value.get('ID') }); // TODO: workaround, change ID to id on server-side

							var postMarkup = '	<li class="manage-post-item" data-id="'+value.get('ID')+'">\
													<h1>'+value.get('title')+'</h1>\
													<h2>'+value.get('subtitle').substr(0, 75)+'...</h2>\
													<a href="#" class="edit-button"><i class="icon-uniE640"></i></a>\
												</li>';
							$('.post-list').append(postMarkup);
						});

						// show editing view when post is clicked by user
						$('.manage-post-item').click(function() {
							_.each(_this.userPosts.models, function(value, key, list) {
								if (value.get('ID') == $(this).data('id')) {
									_this.showEditorView(null, value);
								}
							}, this);
						});

						logger('[User Posts Collection Fetch] Fetched all posts from current user.', 'info');
					},
					error: function(collection, response, options) {
						logger('[User Posts Collection Fetch] Oh oh! Could not fetch user posts. Most likely a server issue. Response: '+response, 'error');
					}
				});

				this.sidebarView.loadViewState('homepage');
				this.contentView.loadViewState('manage');
			},
			showPostView: function() {
				logger('[View Changed] Post View', 'info');
				this.sidebarView.loadViewState('post');
				this.contentView.loadViewState('post');
			},
			setupMedium: function() {
				// set up medium.js for editor view
				new Medium({
	                element: document.getElementById('post-title'),
	                debug: debug,
				    modifier: 'auto',
				    placeholder: "Your Title...",
				    autofocus: true,
				    mode: 'inline',
	            });

	            new Medium({
	                element: document.getElementById('post-subtitle'),
	                debug: debug,
				    modifier: 'auto',
				    placeholder: "Your longer subtitle or preface...",
				    autofocus: false,
				    mode: 'inline',
	            });

				new Medium({
	                element: document.getElementById('post-body'),
	                debug: debug,
				    modifier: 'auto',
				    placeholder: "Start writing here...",
				    autofocus: false,
				    autoHR: false,
				    mode: 'rich',
				    modifiers: {
				        66: 'bold',
				        73: 'italicize',
				        85: 'underline',
				        86: 'paste'
				    },
				    tags: {
				        paragraph: 'p',
				        outerLevel: ['blockquote', 'figure', 'h1', 'h2'],
				        innerLevel: ['a', 'b', 'u', 'i', 'img', 'strong']
				    }
	            });
			},
			saveCurrentPost: function() {
				// create a new post?
				if (!this.currentPost) {
					this.currentPost = new Post({ authorId: this.currentUser.get('id') });
				}

				// update post with form data from editor view
				this.currentPost.set({
					title: 		$('#post-title').text(),
					subtitle: 	$('#post-subtitle').text(),
					body: 		$('#post-body').html()
				});

				// push updated post to server
				this.currentPost.save();
			}
		});

		// fire up the the app
		var App = new AppView();

		return {
			// public functions go here
		};
		
	}());
});