## contributing

#### feedback

- feedback is welcome
- create issues for bugs, enhancements, questsions etc.

#### pull requests

- create pull requests into the `master` branch


#### roadmap

- [x] enjoy the heck out of building with clojure 
- [x] try editing a feature, will it work at all
- [x] don't configure geoserver, connect as-is to ensure ged works as a tool
- [x] use shadow-cljs dev server and custom handler to proxy requests
- [x] extend Response protocol to proxy bite-arrays (for /wms)
- [x] use code splitting to lazy-load routes 
- [x] create map once, only change target on mount/unmount
- [x] use ant design because it's a powerful abstraction
- [x] use events and coeffects to read/update stateful state (ace editors and openlayers' instances)
- [x] place stateful state into core.cljs files
- [x] implement editing feature types (layers) using geoserver's /rest
- [x] implement viewing map layers, use ::sync-layers over react components to maintain map state when chaging page
- [x] implement all layers client side search (tested on 30000+ layers)
- [x] implement WFS search via selecting search attributes or use ecql filter directly
- [x] add geometry name as a setting
- [x] add /wms proxying as a setting
- [x] use centralized log to register and inspect http requests
- [x] implememnt profiles to easily switch between multiple geoservers, use simple indexes
- [x] remove origin header on proxy-requests and add 'allow *' on proxy-responses  
- [x] implement graphical feature editing using openlayers Modify, allow find/modify/find more/modify
- [x] implement editing feature from map's WFS search
- [x] add help info to /feats and /auth
- [x] add development guide doc
- [x] add contributing doc
- [x] add features doc
- [ ] add clojure spec
- [ ] build an http absctraction to resolve http dependencies lazily (or wait for https://github.com/Day8/re-frame-http-fx-2)
- [ ] implement proper centralized error handling to prevent app from crashing (crash only a route, display a message)
- [ ] smaller production docker image
- [ ] ? use a stand-alone written-in-clojure reusable web server for production (instead of shadow)
- [ ] ? adopt transit
- [ ] add screenshots to development guide
- [ ] rename various *-input *-id input components to a consistent name
- [ ] don't store passwords in local storage
- [ ] creating features on the map 
- [ ] add tests


#### stages

- [x] stage 1

    * build reference implemetation to edit layer from an interface 
    * if possible, go to stage 2

- [x] stage 2

    * design editing interface

- [x] stage 3

    * build a usable tool (minimum working product)

- [ ] stage 4

    * step back, redesign
    * improve docs
