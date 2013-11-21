// load the application when DOM is ready (using jQuery)
$(function() {
	"use strict";

	var logger = function(output) {
		var date = new Date();
		var h = date.getHours();
		var m = date.getMinutes();
		var s = date.getSeconds();

		if (h < 10)	h = '0'+h;
		if (m < 10)	m = '0'+m;
		if (s < 10)	s = '0'+s;

    	console.log(h+':'+m+':'+s+' '+output);
	}

	// module pattern, uses own namespace
	var feder = (function() {

		/*
		 * Post Model
		 * model to represent single posts
		 */
		var Post = Backbone.Model.extend({
			urlRoot: 'http://localhost:8080/posts',
	        defaults: {
	        	authorId: '',
	            title: '',
	            subtitle: '',
	            body: '',
	            image: '',
	            creationDate: '',
	            collectionId: -1,
	            isDraft: true
	        },
	        initialize: function() {
	        	logger('[Post Created] '+JSON.stringify(this.toJSON()));
	        }
	    });

		/*
		 * AppView
		 * the main view for our application
		 */
		var AppView = Backbone.View.extend({
			el: $('body'),
			events: {

			},
			initialize: function() {
				logger('Feder App started.');

				var samplePost = new Post({ id: 0, title: 'Sample Post' });

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
	            $('a').click(function(event) {
					event.preventDefault();
					$(this).modal({
						fadeDuration: 150
					});
				});
			},
			render: function() {

			}
		});

		// fire up the the app
		var App = new AppView();

		return {
			// public functions go here
		};

	}());
});