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

		var testCollections = [{"id":0,"title":"Technology","image":"","views":100,"posts":18},{"id":1,"title":"Science","image":"","views":24,"posts":18},{"id":2,"title":"Food","image":"","views":123,"posts":18},{"id":3,"title":"Sports","image":"","views":0,"posts":18},{"id":4,"title":"Comedy","image":"","views":0,"posts":18}];

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
		 * Collection Collection ;P
		 * collection of post collections from the server
		 */
	    var Collections = Backbone.Collection.extend({
			model: Collection,
			url: _urlRoot+'/collections'
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
				'click .post':				'showEditorView'	// TODO: for test purposes, change to showPostView later
			},
			initialize: function() {
				logger('Feder App started.', 'info');

				// sample model instantiation
				this.user = new User({ id: 0, username: 'Philipp' });
				this.post = new Post({ id: 0, title: 'Sample Post', subtitle: 'Blubb', body: 'Lorem ipsum...' });
				// var sampleComment = new Comment({ id: 0 });

				// collection of post collections instance
				this.collections = new Collections(testCollections);
				// collections.fetch();

				// sort collections by views (descending)
				this.collections.models = _.sortBy(this.collections.models, function(o) { return o.get('views'); });
				this.collections.models.reverse();

				grande.bind(document.querySelectorAll("article"));

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

				// TODO: display recent and popular posts

				$('.editor-view').fadeOut(500);
				$('.reading-view').fadeIn(500);
				$('.home-view').fadeIn(500);
				$('.collections-view').fadeOut(500);
			},
			showCollectionView: function() {
				logger('[View Changed] Collection View', 'info');

				$('.collections').empty();

				// create markup for collection list
				_.each(this.collections.models, function(value, key, list) {
					$('.collections').append('<a class="collection" href="#">'+value.get('title')+' <span>'+value.get('posts')+' Posts</span></a>')	
				}, this);

				$('.editor-view').fadeOut(500);
				$('.reading-view').fadeIn(500);
				$('.home-view').fadeOut(500);
				$('.collections-view').fadeIn(500);
			},
			showCollectionDetailView: function() {
				logger('[View Changed] Collection Detail View', 'info');

				// TODO: show recent and popular posts of selected collection

				$('.editor-view').fadeOut(500);
				$('.reading-view').fadeIn(500);
				$('.home-view').fadeIn(500);
				$('.collections-view').fadeOut(500);
			},
			showEditorView: function() {
				logger('[View Changed] Editor View', 'info');

				// TODO: fetch real post data and set post variable

				// fill editor form with post data
				$('.article-title').text(this.post.get('title'));
				$('.article-subtitle').text(this.post.get('subtitle'));
				$('.article-body').text(this.post.get('body'));

				$('.reading-view').fadeOut(500);
				$('.editor-view').fadeIn(500);
			},
			showPostView: function() {
				logger('[View Changed] Post View', 'info');

				// TODO: show selected post

				$('.reading-view').fadeIn(500);
				$('.editor-view').fadeOut(500);
			}
		});

		// fire up the the app
		var App = new AppView();

		return {
			// public functions go here
		};
		
	}());
});