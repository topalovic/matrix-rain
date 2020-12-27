# Matrix rain

The effect has been [clubbed to death](https://vimeo.com/58527998), but I had to give it a whirl in ClojureScript.

You can see the live demo [here](https://topalovic.github.io/matrix-rain/).

## Goals

* write idiomatic Clojure without mutation shortcuts, so no `object-array` and such
* zero dependencies, so no Quill, `clojure.core.matrix`, etc
* tight code without getting (too) obtuse
* visually close to the movie rendition

## Setup

To launch the project and enter a REPL:

```sh
clj -m cljs.main -c matrix-rain.core -r
```

For an optimized build:

```sh
clj -m cljs.main -O advanced -c matrix-rain.core
```

then open `index.html` which will load the optimized `out/main.js`.

Enjoy!
