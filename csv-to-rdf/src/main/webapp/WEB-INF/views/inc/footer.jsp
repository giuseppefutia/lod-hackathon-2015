<%@page session="true"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@taglib uri="http://www.springframework.org/tags" prefix="sp"%><footer>
	<div id="download">
		<a href="https://github.com/dvcama/LodView" target="_blank"><sp:message code='footer.download' text='download lodview to publish your data' /></a>
	</div>
	<div id="endpoint">
		<ul>
			<c:choose>
				<c:when test='${conf.getEndPointUrl().equals("<>")}'>
					<li><sp:message code='footer.noSparql' text='data from: deferencing IRI' /></li>
				</c:when>
				<c:otherwise>
					<li><sp:message code='footer.yesSparql' text='data from:' /> <a href="${conf.getEndPointUrl()}">${conf.getEndPointUrl()}</a></li>
				</c:otherwise>
			</c:choose>
			<li><a target="_blank" href="${lodliveUrl }"><sp:message code='footer.viewLodlive' text='view on lodlive' /></a></li>
			<li class="viewas"><span><sp:message code='footer.viewAs' text='view as' /></span> <a title="application/rdf+xml">xml</a>, <a title="text/plain">ntriples</a>, <a title="text/turtle">turtle</a>, <a title="application/ld+json">ld+json</a></li>
		</ul>
	</div>
</footer>
<script>
	var col1 = 0;
	var col3 = 0;
	var paginator = {
		"<sp:message code='label.toTop' text='to top' javaScriptEscape='true' />" : $('body'),
		"<sp:message code='title.directRelations' text='direct relations'  javaScriptEscape='true' />" : null,
		"<sp:message code='title.blankNodes' text='blank nodes' javaScriptEscape='true' />" : null,
		"<sp:message code='title.inverse' text='inverse relations' javaScriptEscape='true'/>" : null,
		"<sp:message code='title.lodCloud' text='data from the linked data cloud' javaScriptEscape='true' />" : null
	};

	$('#logo').click(function() {
		document.location = '${conf.getPublicUrlPrefix()}';
	});

	function setColumnsSize(secondTrip) {
		$('.c1').each(function() {
			var s = $(this).width();
			if (secondTrip) {
				// occurs after web font rendering
				s = s - 8;
			}
			if (s > col1) {
				col1 = s;
			}
		});
		$('.c2').animate({
			// 24 = main padding
			marginLeft : col1 + 24,
			marginTop : 0
		}, 'slow', 'swing', function() {
			$('.c2').css({
				visibility : 'visible'
			});
		});
		$('.c3').each(function() {
			var s = $(this).width();
			if (s > col3) {
				col3 = s;
			}
		});
		$('.c4').each(function() {
			var h = $(this).prev('.c3').height();
			$(this).animate({
				// 24 = main padding
				marginLeft : col3 + 24,
				marginTop : 0
			}, 'slow');
		});
	}
	$(function() {

		/* error images */
		setErrorImage($('#widgets').find('img'));

		/* improving interface*/
		setColumnsSize();
		betterHeader();
		$(window).on('resize', function() {
			betterHeader();
		});

		/* footer functions */
		footer();

		/* managing languages */
		multiLabels();

		/* recovering connected titles from relations */
		connectedResourceTitles();

		/* navigation tool */
		navigator();

		/* grabbing informations from the LOD cloud */
		grabData();

		$(window).on('load', function() {
			/* removing lodCloud block if empty */
			if ($('.linkingElement').length > 0) {
				paginator["<sp:message code='title.lodCloud' text='data from the linked data cloud' javaScriptEscape='true' />"] = $('#lodCloud');
			}
			if ($('#directs').children(":first").length == 0) {
				$('#directs').addClass("empty");
			} else {
				paginator["<sp:message code='title.directRelations' text='direct relations'  javaScriptEscape='true' />"] = $('#directs');
			}
			if ($('#bnodes').not('.empty').length > 0) {
				paginator["<sp:message code='title.blankNodes' text='blank nodes'  javaScriptEscape='true' />"] = $('#bnodes');
			}
		});
	});

	function betterHeader() {
		var IRI = $('h2>.iri');
		var istance = $('h2>.istance');
		var istsize = 0;
		istance.find('a').each(function() {
			istsize += $(this).width();
		});
		if (window.innerWidth - IRI.width() - istsize < 250) {
			istance.css({
				"float" : "none",
				"text-align" : "left",
				"display" : "block"
			});
		} else {
			istance.css({
				"float" : "right",
				"text-align" : "right",
				"display" : "inline-block"
			});
		}
	}

	function connectedResourceTitles() {
		lMessage("<sp:message code='message.loadingInverses' text='loading inverse relations' javaScriptEscape='true'/>", 'open');
		var abouts = [];
		$('a.isLocal').each(function() {
			var a = $(this).attr('title').replace(/[><]/g, '');
			if ($.inArray(a, abouts) == -1) {
				abouts.push(a);
			}
		});
		if (abouts.length > 0) {
			$('#lconnected').fadeIn('fast');
			doConnectedResourceTitles(abouts, function() {
				getInverses()
			});
		} else {
			getInverses();
		}
	}

	function doConnectedResourceTitles(abouts, onComplete) {
		return $.ajax({
			url : "${conf.getPublicUrlPrefix()}linkedResourceTitles",
			data : {
				"abouts" : abouts,
				"IRI" : "${results.getMainIRI()}",
				"sparql" : "${conf.getEndPointUrl()}",
				"prefix" : "${conf.getIRInamespace()}"
			},
			method : 'POST',
			beforeSend : function() {

			},
			success : function(data) {
				data = $(data);
				data.find('resource').each(function() {
					var IRI = $(this).attr("about");
					var title = $(this).find("title").text();
					$('a.isLocal[title="<' + IRI + '>"]').each(function() {
						if ($(this).find('tt').length == 0) {
							$(this).append("<br><span class='derivedTitle'><tt class=\"sp\"></tt>" + title + "</span>");
						}
					})
				});
			},
			error : function(e) {
				/* TODO: manage errors */
			},
			complete : function() {
				/* inverse relations*/
				if (onComplete) {
					onComplete();
				}
			}
		});
	}

	function multiLabels() {
		var cLocale = '${locale}';
		$('.value').each(function() {
			var cnt = $(this);
			var multipleLang = false;
			var plang = "";
			$('.lang', cnt).each(function() {
				var lang = $(this).attr("data-lang");
				if (lang != plang && plang != '') {
					multipleLang = true;
				}
				plang = lang;
			});
			if (multipleLang) {
				$('.lang', cnt).each(function() {
					var lang = $(this).attr("data-lang");
					if ($.trim(lang) && cnt.find("span.clang." + lang).length == 0) {
						var clang = $("<span class=\"clang "+lang+"\">" + lang + "</span>");
						clang.click(function() {
							var lang = $(this).text();
							$(this).parent().children('div').hide();
							$(this).parent().children('div.lang.' + lang).show();
							$(this).parent().children('span.clang').removeClass('sel');
							$(this).addClass('sel');
						});
						cnt.find("div:first").before(clang);
					}
				});
				cnt.find('.clang').sort(function(a, b) {
					var contentA = $(a).text();
					var contentB = $(b).text();
					return (contentA < contentB) ? -1 : (contentA > contentB) ? 1 : 0;
				}).prependTo(cnt);

				var btt = cnt.children('span.clang.' + cLocale);
				if (btt.length == 0) {
					btt = cnt.children('span.clang.en'); //fallback to english
				}
				if (btt.length == 0) {
					btt = cnt.children('span.clang:first');
				}
				btt.click();
			} else {
				$('.lang', cnt).each(function() {
					var lang = $(this).attr("data-lang");
					if ($.trim(lang)) {
						var a = $(this);
						if (a.find("div.fixed").length > 0) {
							a = a.find(".fixed");
						}
						a.append("<span class=\"elang\">@" + lang + "</span>");
					}
					$(this).removeClass('lang');
				});
			}
		});
	}

	function getInverses() {
		lMessage("<sp:message code='message.loadingConnected' text='loading connected resource titles' javaScriptEscape='true'/>");
		var invCont = $('#inverses');
		var iri = "${results.getMainIRI()}";
		if (iri) {
			$('#linverse').delay(100).fadeIn('fast');
			$.ajax({
				url : "${conf.getPublicUrlPrefix()}linkedResourceInverses",
				method : 'POST',
				data : {
					"IRI" : iri,
					"sparql" : "${conf.getEndPointUrl()}",
					"prefix" : "${conf.getIRInamespace()}"
				},
				beforeSend : function() {

				},
				success : function(data) {
					data = $(data);

					if (data.find('resource').length > 0) {
						invCont.removeClass("empty");
						invCont.append($("<h3><sp:message code='title.inverse' text='inverse relations' javaScriptEscape='true'/></h3>"));
						paginator["<sp:message code='title.inverse' text='inverse relations' javaScriptEscape='true'/>"] = invCont;
					}
					data.find('resource').each(function() {

						var IRI = $(this).attr("nsabout");
						if (IRI.indexOf("null:") == 0) {
							IRI = '&lt;' + $(this).attr("about").replace(/(http:\/\/.+[/#])([^/#]+)$/, '$1<span>$2') + "</span>&gt;";
						} else {
							IRI = IRI.replace(/:/, ':<span>') + '</span>';
						}
						var count = $(this).find("count").text();
						var msg = "<sp:message code='label.inverseProperty' text='is {0} of' javaScriptEscape='true' />";
						// TODO: add link!
						msg = msg.replace(/\{0\}/, "<a title=\"&lt;" + $(this).attr("about") + "&gt;\">" + IRI);

						var el = $("<label class=\"c1\" title=\"" + $(this).attr("about") + "\">" + msg + "</label>");
						var anchor = $("<a href=\"#openIt\" data-property=\"" + $(this).attr("about") + "\">" + count + " " + (count == 1 ? "<sp:message code='label.resource' text='resource' javaScriptEscape='true'/>" : "<sp:message code='label.resources' text='resources' javaScriptEscape='true'/>") + "</a>");
						anchor.click(function() {
							if (anchor.parent().hasClass('isOpened')) {
								anchor.parent().find('.toOneLine,.prevArrow,.nextArrow,.lloadingb').remove();
								anchor.parent().removeClass('isOpened');
								anchor.parent().removeClass('opened');
							} else {
								var property = $(this).attr('data-property');
								var contInverse = $(this).parent();
								anchor.parent().addClass("isOpened");
								var start = 0;
								$.ajax({
									url : "${conf.getPublicUrlPrefix()}linkedResourceInverses",
									method : 'POST',
									data : {
										"start" : start,
										"IRI" : "${results.getMainIRI()}",
										"property" : property,
										"sparql" : "${conf.getEndPointUrl()}",
										"prefix" : "${conf.getIRInamespace()}"
									},
									beforeSend : function() {
										anchor.parent().find('.toOneLine,.prevArrow,.nextArrow,.lloadingb').remove();
										anchor.after("<span class=\"lloadingb\"></span>");
									},
									success : function(data) {
										data = $(data);
										var abouts = [];
										data.find('resource').each(function() {
											var IRI = $(this).attr("nsabout");
											if (IRI.indexOf("null:") == 0) {
												IRI = '&lt;' + $(this).attr("about") + "&gt;";
											}
											if (IRI.indexOf(":") == -1) {
												IRI = "_:" + $(this).attr("about");
											}
											var title = $(this).find("title").text();
											var url = $(this).attr("propertyurl")
											contInverse.append($("<div class='toOneLine' style='display:none'> <a title=\"&lt;" + $(this).attr("about") + "&gt;\" href=\"" + url + "${suffix}\" class=\"isLocal\">" + IRI + "</a></div>"));
											abouts.push($(this).attr("about"));
										});
										$('.toOneLine', contInverse).show();
										if (count > 10) {
											anchor.parent().addClass("opened");
										}
										if (abouts.length > 0) {
											doConnectedResourceTitles(abouts, function() {
												anchor.parent().find('.lloadingb').hide();
											});
										} else {
											anchor.parent().find('.lloadingb').hide();
										}
									},
									complete : function() {
										if (count > 10) {
											var prev = $('<a href="#prev" class="prevArrow sp"></a>');
											var next = $('<a href="#next" class="nextArrow sp"></a>');
											prev.css({
												'opacity' : '0.3',
												'cursor' : 'default'
											});
											next.click(function() {
												return paginating('next', $(this), start, property, count);
											});
											anchor.after(next);
											anchor.after(prev);
										}

									},
									error : function() {
										contInverse.append($("<div class='toOneLine' >sorry, an error occurred</div>"));
									}

								});
							}
							return false;
						});
						invCont.append(el);
						var value = $("<div class=\"c2 value\"></div>");
						value.append(anchor);
						invCont.append(value);
						if ("open" == "${conf.getDefaultInverseBehaviour()}") {
							anchor.click();
						}
					});
					setColumnsSize(true);
					$('#linverse').append('<span class="ok"><img src="${conf.getStaticResourceURL()}img/checked' + (isRetina ? '@2x' : '') + '.png" ></span>').find('img').fadeIn('fast');
				},
				error : function(e) {
					$('#linverse').append('<span class="error"></span>');
				},
				complete : function() {
					lMessage(null, 'close');
				}
			});
		}

	}

	var callingPage = null;
	var callingPageTitles = null;

	function paginating(direction, anchor, start, property, count) {
		if (direction == 'next') {
			start = start + 10;
		} else if (start > 0) {
			start = start - 10;
		}
		var contInverse = anchor.parent();
		if (callingPage) {
			callingPage.abort();
		}
		if (callingPageTitles) {
			callingPageTitles.abort();
		}
		callingPage = $.ajax({
			url : "${conf.getPublicUrlPrefix()}linkedResourceInverses",
			method : 'POST',
			data : {
				"start" : start,
				"IRI" : "${results.getMainIRI()}",
				"property" : property,
				"sparql" : "${conf.getEndPointUrl()}",
				"prefix" : "${conf.getIRInamespace()}"
			},
			beforeSend : function() {
				contInverse.find('.toOneLine').addClass('toRemove').css({
					'opacity' : 0.2
				});
				contInverse.find('.prevArrow,.nextArrow').remove();
				contInverse.find('.lloadingb').show();
				var prev = $('<a href="#prev" class="prevArrow sp"></a>');
				var next = $('<a href="#next" class="nextArrow sp"></a>');
				console.info(start + " " + count)
				if (start + 10 > count) {
					next.css({
						'opacity' : '0.3',
						'cursor' : 'default'
					});
				} else {
					next.click(function() {
						return paginating('next', $(this), start, property, count);
					});
				}
				if (start > 0) {
					prev.click(function() {
						return paginating('prev', $(this), start, property, count);
					});
				} else {
					prev.css({
						'opacity' : '0.3',
						'cursor' : 'default'
					});
				}
				contInverse.find('a:first').after(next);
				contInverse.find('a:first').after(prev);
			},
			success : function(data) {
				data = $(data);
				var abouts = [];
				data.find('resource').each(function() {
					var IRI = $(this).attr("nsabout");
					if (IRI.indexOf("null:") == 0) {
						IRI = '&lt;' + $(this).attr("about") + "&gt;";
					}
					if (IRI.indexOf(":") == -1) {
						IRI = "_:" + $(this).attr("about");
					}
					var title = $(this).find("title").text();
					var url = $(this).attr("propertyurl")
					contInverse.append($("<div class='toOneLine' style='display:none'> <a title=\"&lt;" + $(this).attr("about") + "&gt;\" href=\"" + url + "${suffix}\" class=\"isLocal\">" + IRI + "</a></div>"));
					abouts.push($(this).attr("about"));
				});

				if (abouts.length > 0) {
					callingPageTitles = doConnectedResourceTitles(abouts, function() {
						contInverse.find('.toOneLine.toRemove').remove();
						$('.toOneLine', contInverse).show();
						contInverse.find('.lloadingb').hide();
					});
				} else {
					contInverse.find('.toOneLine.toRemove').remove();
					$('.toOneLine', contInverse).show();
					contInverse.find('.lloadingb').hide();
				}
				anchor.unbind('click');
			},
			complete : function() {
			},
			error : function() {
				contInverse.find('.toOneLine').remove();
				contInverse.append($("<div class='toOneLine' >sorry, an error occurred</div>"));
			}
		});
		return false;
	}

	function grabData() {
		var linkingList = [];
		var counter = 0;
		var errors = 0;
		var map = $('map:first');
		$('.linkingElement').each(function() {
			var link = $.trim($(this).attr("href"));
			linkingList.push(link);
		});

		/* lod cloud */
		if (linkingList.length == 0) {
			$('#lodCloud').empty();
			$('#lodCloud').addClass("empty");
			$('#linking').hide();
		} else {
			$('#linking').fadeIn('fast');
			var container = $('#lodCloud').children("div");
			var dest = $('<div class="connected"><span class="lloading"></span></div>');
			var content = $("<div class=\"content\" id='counterBlock'></div>");

			content.append("<p id='grabDataTotal'><sp:message code='message.grabDataTotal' text='Resource connected {0}' arguments='<strong>0</strong>' javaScriptEscape='true' /></p>")
			content.append("<p id='grabDataTotalErrors'><sp:message code='message.grabDataTotalErrors' text='Resource not online {0}' arguments='<strong>0</strong>' javaScriptEscape='true' /></p>")
			content.append("<p id='grabDataTotalLoaded'><sp:message code='message.grabDataTotalLoaded' text='Resource loaded {0}' arguments='<strong>0</strong>' javaScriptEscape='true' /></p>")

			dest.append(content);
			container.append(dest);

			// initialize
			container.masonry({
				itemSelector : '.connected'
			});
			// var msg = "<sp:message code='message.grabData' text='retrieving data from the LOD cloud (<strong>0</strong> of <span>0</span>)' arguments='<strong>0</strong>,<span>0</span>' javaScriptEscape='true' />";
			// msg = msg.replace(/<span>0<\/span>/, '<span>' + linkingList.length + '</span>');
			// lMessage(msg);
			updateCounter(linkingList.length, counter, errors);
			grabSingleResource(linkingList, counter, errors, map);
		}
	}

	function grabSingleResource(linkingList, counter, errors, map) {
		if (counter < linkingList.length) {
			var linking = $('#lodCloud').children("div");
			$.ajax({
				url : "${conf.getPublicUrlPrefix()}linkedResource${suffix}",
				method : 'POST',
				timeout : 10000, //5 sec.
				data : {
					"IRI" : linkingList[counter]
				},
				beforeSend : function() {
					//console.debug(counter + " -- " + linkingList[counter])
				},
				success : function(data) {
					data = $(data);
					var dest = $('<div class="connected"></div>');
					data.find('img:first').each(function() {
						dest.append("<span class=\"imgCnt sp\"><img class=\"main\" src=\"" + $(this).attr("src") + "\"></span>");
					});
					var title = (data.find('title').text() != 'null' ? data.find('title').text() : "");

					if (data.find('img:first').length == 0 && data.find('longitude').text().replace(/null/, '') && data.find('latitude').text().replace(/null/, '')) {
						var id = new Date().getMilliseconds();
						var map = $('<map  id="inlodMap'+id+'" class="inLodMap sp"></map>');
						dest.append(map);
					}

					setErrorImage(dest.find('img'));

					var content = $("<div class=\"content\"></div>");
					content.append("<h5>" + title + "</h5>");

					var IRI = data.find('root').attr('about');
					content.append("<a class=\"link\" target=\"_blank\" href=\"" + IRI + "\">" + IRI.replace(/([^a-zA-Z0-9])/g, '$1&#8203;') + "</a>");
					if ($.trim(data.find('description').text())) {
						var descr = data.find('description').text() + " @" + data.find('description').attr("lang");
						descr = descr.replace(/@(null|)$/, '');
						content.append("<div>" + descr + "</div>");
					}
					dest.append(content);

					var tot = data.find('links').attr("tot");
					if (parseInt(tot, 10) > 0) {
						dest.append("<div class=\"more\">" + tot + " " + (tot == 1 ? "<sp:message code='label.connectedResource' text='connected resource' javaScriptEscape='true' />" : "<sp:message code='label.connectedResources' text='connected resources' javaScriptEscape='true' />") + "</div>")
					}
					dest.find('img').on('load', function() {
						linking.masonry();
					}).each(function() {
						if (this.complete) {
							$(this).load();
						}
					});
					counter++;
					if (!data.find('root').attr('error')) {
						linking.masonry().append(dest).masonry('appended', dest);
						if (dest.find('map').length > 0) {
							drawMap(dest.find('map').attr("id"), data.find('latitude').text(), data.find('longitude').text(), title);
						}
						data.find('link').each(function() {
							var a = $(this).attr("href");
							if ($.inArray(a, linkingList) == -1 && a != "${results.getMainIRI()}") {
								linkingList.push(a);
							}
						});
					} else {
						errors++;
					}
					linking.masonry();
					grabSingleResource(linkingList, counter, errors, map);
					updateCounter(linkingList.length, counter, errors);
				},
				error : function(e) {
					updateCounter(linkingList.length, (counter + 1), (errors + 1));
					grabSingleResource(linkingList, (counter + 1), (errors + 1), map);
				},
				complete : function() {
				}
			});
		} else {
			$('.connected').find('.lloading').fadeOut();
		}
	}

	function lMessage(msg, action) {
		var lp = $('#loadPanel');
		if (action) {
			if (action == 'open') {
				if (isChrome) {
					// moving the panel to the center of the page
					lp.addClass("cfix");
				}
				lp.fadeIn('fast');
			} else {
				lp.delay(2000).fadeOut('fast');
			}
		}
		if (msg) {
			lp.queue(function() {
				setTimeout(function() {
					lp.find('.content').remove();
					lp.find('#lmessage').append("<span class=\"content\">" + msg + "</span>");
					lp.dequeue();
				}, action == 'open' ? 0 : 2000);
			});
		}
	}

	function slideNext(obj) {
		obj.next().slideDown('fast', function() {
			slideNext($(this));
		});
	}
	function updateCounter(tot, count, errors) {
		var g = $('#lmessage > .content');
		g.children('strong').html(count);
		g.children('span:last').html(tot);
		var b = $('#counterBlock');
		b.find('p#grabDataTotal > strong').html(tot);
		b.find('p#grabDataTotalLoaded > strong').html(count - errors);
		b.find('p#grabDataTotalErrors > strong').html(errors);
	}

	function footer() {
		$('#endpoint').find('.viewas').find('a').click(function() {
			var loc = document.location.href;
			loc = loc.replace(/#[^#]+/, '');
			if (loc.indexOf("?") != -1) {
				loc += "&output=" + $(this).attr("title");
			} else {
				loc += "?output=" + $(this).attr("title");
			}
			document.location = loc;
		});
	}

	function setErrorImage(obj) {
		obj.error(function() {
			$(this).attr("title", "<sp:message code='message.noImage' text='image not available, broken URL?' javaScriptEscape='true' />\n" + $(this).attr("src"));
			$(this).attr("src", "${conf.getStaticResourceURL()}img/no_image" + (isRetina ? "@2x" : "") + ".png");
			$(this).unwrap("a");
		});
	}

	function navigator() {
		$('#navigator').find('.top').hover(function() {
			var e = $(this);
			if (window.scrollY > 0) {
				e.addClass('hover');
				var a = $('<a href="#top"></a>');
				a.click(function() {
					$('body').scrollTo(0, {}, function() {
						a.parent().trigger('mouseleave');
						a.parent().trigger('mouseenter');
					});
					return false;
				});
				e.prepend(a);
				e.prepend("<span><sp:message code='label.toTop' text='to top' javaScriptEscape='true' /></span>");
			}
		}, function() {
			$(this).removeClass('hover');
			$(this).find('span , a').remove();
		});
		$('#navigator').find('.up').hover(function() {
			var y = window.scrollY;
			var prev = null;
			var e = $(this);
			$.each(paginator, function(k, v) {
				if (v && y > v.position().top) {
					prev = {};
					prev[k] = v;
				}
			});
			if (prev) {
				$.each(prev, function(k, v) {
					e.addClass('hover');
					var a = $('<a href="#' + v.attr("id") + '"></a>');
					a.click(function() {
						$('body').scrollTo(v.position().top, {}, function() {
							a.parent().trigger('mouseleave');
							a.parent().trigger('mouseenter');
						});
						return false;
					});
					e.prepend(a);
					e.prepend('<span>' + k + '</span>');
				});
			}
		}, function() {
			$(this).removeClass('hover');
			$(this).find('span , a').remove();
		});
		$('#navigator').find('.down').hover(function() {
			var y = window.scrollY + 1;
			var next = null;
			var e = $(this);
			$.each(paginator, function(k, v) {
				if (!next && v && y < v.position().top && y + window.innerHeight < $(document).height()) {
					next = {};
					next[k] = v;
				}
			});
			if (next) {
				$.each(next, function(k, v) {
					e.addClass('hover');
					var a = $('<a href="#' + v.attr("id") + '"></a>');
					a.click(function() {
						$('body').scrollTo(v.position().top, {}, function() {
							a.parent().trigger('mouseleave');
							a.parent().trigger('mouseenter');
						});
						return false;
					});
					e.prepend(a);
					e.prepend('<span>' + k + '</span>');
				});
			}
		}, function() {
			$(this).removeClass('hover');
			$(this).find('span , a').remove();
		});
		$.fn.scrollTo = function(target, options, callback) {
			if (typeof options == 'function' && arguments.length == 2) {
				callback = options;
				options = target;
			}
			var settings = $.extend({
				scrollTarget : target,
				offsetTop : 50,
				duration : 200,
				easing : 'swing'
			}, options);
			return this.each(function() {
				var scrollPane = $(this);
				var scrollTarget = (typeof settings.scrollTarget == "number") ? settings.scrollTarget : $(settings.scrollTarget);
				var scrollY = (typeof scrollTarget == "number") ? scrollTarget : scrollTarget.offset().top + scrollPane.scrollTop() - parseInt(settings.offsetTop);
				scrollPane.animate({
					scrollTop : scrollY
				}, parseInt(settings.duration), settings.easing, function() {
					if (typeof callback == 'function') {
						callback.call(this);
					}
				});
			});
		}
	}
</script>