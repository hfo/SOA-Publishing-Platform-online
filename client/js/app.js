// load the application when DOM is ready (using jQuery)
$(function() {
	"use strict";

	var logger = function(output, type) {
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

	// module pattern, app namespace
	var feder = (function() {

		var _urlRoot = 'http://localhost:8080';

		/*
		 * User Model
		 * model to represent the signed in user
		 */
		var User = Backbone.Model.extend({
			urlRoot: _urlRoot+'/users',
			defaults: {
				username: '',
				email: ''
			},
			initialize: function() {
				logger('[User Instance Created] '+JSON.stringify(this.toJSON()), 'info');
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
	            isDraft: true,
	            views: 0
	        },
	        initialize: function() {
	        	logger('[Post Instance Created] '+JSON.stringify(this.toJSON()), 'info');
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
	        	logger('[Comment Instance Created] '+JSON.stringify(this.toJSON()), 'info');
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
	    		logger('[Collection Instance Created] '+JSON.stringify(this.toJSON()), 'info');
	    	}
	    });

		/*
		 * AppView
		 * the main view for our application
		 */
		var AppView = Backbone.View.extend({
			el: $('body'),
			events: {
				'click .home-link':			'showHomeView',
				'click .collections-link':	'showCollectionView',
				'click .collection':		'showCollectionDetailView',
				'click .post':				'showPostView'
			},
			initialize: function() {
				logger('Feder App started.', 'info');

				// sample model instantiation
				var sampleUser = new User({ id: 0, username: 'Philipp' });
				var sampleCollection = new Collection({ id: 0, title: 'Sample Collection' });
				var samplePost = new Post({ id: 0, title: 'Sample Post' });
				var sampleComment = new Comment({ id: 0 });

				// set up medium.js for editor view
				new Medium({
	                element: document.getElementById('article-title'),
	                debug: true,
				    modifier: 'auto',
				    placeholder: "Your Title",
				    autofocus: true,
				    mode: 'inline',
	            });

	            new Medium({
	                element: document.getElementById('article-subtitle'),
	                debug: true,
				    modifier: 'auto',
				    placeholder: "Optional Subtitle",
				    autofocus: false,
				    mode: 'inline',
	            });

				new Medium({
	                element: document.getElementById('article-body'),
	                debug: true,
				    modifier: 'auto',
				    placeholder: "Start writing here...",
				    autofocus: false,
				    autoHR: true,
				    mode: 'rich',
				    modifiers: {
				        66: 'bold',
				        73: 'italicize',
				        85: 'underline',
				        86: 'paste'
				    }
	            });

				// jquery modal plugin init
	            $('.modal-link').click(function(event) {
					event.preventDefault();
					$(this).modal({
						fadeDuration: 150
					});
				});
			},
			showHomeView: function() {
				logger('[View Changed] Home View', 'info');

				$('.editor-view').fadeOut(500);
				$('.reading-view').fadeIn(500);
				$('.home-view').fadeIn(500);
				$('.collections-view').fadeOut(500);
			},
			showCollectionView: function() {
				logger('[View Changed] Collection View', 'info');

				$('.editor-view').fadeOut(500);
				$('.reading-view').fadeIn(500);
				$('.home-view').fadeOut(500);
				$('.collections-view').fadeIn(500);
			},
			showCollectionDetailView: function() {
				logger('[View Changed] Collection Detail View', 'info');

				$('.editor-view').fadeOut(500);
				$('.reading-view').fadeIn(500);
				$('.home-view').fadeIn(500);
				$('.collections-view').fadeOut(500);
			},
			showPostView: function() {
				logger('[View Changed] Post View', 'info');

				$('.reading-view').fadeOut(500);
				$('.editor-view').fadeIn(500);
			}
		});

		// fire up the the app
		var App = new AppView();

		return {
			// public functions go here
		};

	}());
});