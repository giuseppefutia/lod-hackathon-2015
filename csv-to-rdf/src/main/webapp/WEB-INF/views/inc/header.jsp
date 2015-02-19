<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@taglib uri="http://www.springframework.org/tags" prefix="sp"%><%@page session="true"%>
<link property="cc:licence" href="http://www.opensource.org/licenses/mit-license.php" />
<link href="${conf.getStaticResourceURL()}reset.css" rel="stylesheet" type="text/css" />
<link href="${conf.getStaticResourceURL()}style.css" rel="stylesheet" type="text/css" />
<script>
	document.write('<style type="text/css">');
	document.write('.c2{visibility:hidden}');
	document.write('</style>');
</script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<!-- adding fonts 
full list of gliphs
-->
<meta property="og:title" content="${results.getTitle()} LodView &mdash; giving data a new shape" />
<meta property="og:description" content="LodView is a powerful RDF viewer, IRI dereferencer and opensource SPARQL navigator" />
<meta property="og:image" content="${conf.getStaticResourceURL()}img/lodview_sharer.png" />
<meta name="twitter:title" content="LodView &mdash; giving data a new shape">
<meta name="twitter:description" content="LodView is a powerful RDF viewer, IRI dereferencer and opensource SPARQL navigator">
<link rel="icon" type="image/png" href="${conf.getStaticResourceURL()}img/favicon.png">
<link href='http://fonts.googleapis.com/css?family=Roboto:100,300,500&subset=latin-ext,latin,greek-ext,greek,cyrillic-ext,vietnamese,cyrillic' rel='stylesheet' type='text/css'>

<!-- managing maps  -->
<link rel="stylesheet" href="${conf.getStaticResourceURL()}leaflet.css" />
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>

<script src="${conf.getStaticResourceURL()}masonry.pkgd.min.js"></script>
<script src="${conf.getStaticResourceURL()}modernizr-custom.min.js"></script>
<c:set var="color1" value='${colorPair.replaceAll("-.+","") }' scope="page" />
<c:set var="color2" value='${colorPair.replaceAll(".+-","") }' scope="page" />
<style type="text/css">
hgroup, #linking a span {
	background-color: ${color1
}

}
header div#abstract, #loadPanel, div#lodCloud .connected div#counterBlock.content
	{
	background-color: ${color2
}

}
#errorPage div#bnodes {
	color: ${color2
}

}
div#loadPanel span.ok img {
	background-color: ${color1
}
}
</style>
<script>
	var isRetina = window.devicePixelRatio > 1;
	var isChrome = /chrom(e|ium)/.test(navigator.userAgent.toLowerCase())
</script>