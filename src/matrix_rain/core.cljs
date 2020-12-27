(ns matrix-rain.core)

(def cvs (.getElementById js/document "matrix"))
(def ctx (.getContext cvs "2d"))

(def font-size 12)
(def w (/ (.-width cvs) font-size))
(def h (/ (.-height cvs) font-size))

(def green 180)
(def katakana (map char (range 0x30a0 0x30ff 2)))
(def glyphs (concat katakana [\: \+ \0 \2 \4 \5 \7 \8 \9 \B \E \F]))

(def cadence 0.012)  ; base speed
(def glitch-rate 10) ; number of glitches per tick

(def state (atom {}))

(defn now [] (.now js/Date))

(defn rand-glyph [] (rand-nth glyphs))

(defn init-matrix []
  (mapv #(mapv rand-glyph (range h)) (range w)))

(defn glitch [matrix]
  (assoc-in matrix [(rand-int w) (rand-int h)] (rand-glyph)))

(defn init-drop []
  {:head  (- (rand-int (* h 2)))
   :size  (+ (rand-int h) (/ h 2))
   :speed (+ (rand) 1.0)})

(defn update-drop [drop tick]
  (let [{:keys [head size speed]} drop
        delta (* speed tick cadence)]
    (if (< head (+ h size))
      (update-in drop [:head] + delta)
      (init-drop))))

(defn init-state []
  {:matrix (init-matrix)
   :drops (mapv init-drop (range w))
   :time (now)
   :tick 0})

(defn update-state [state]
  (let [{:keys [matrix drops time tick]} state]
    {:matrix (nth (iterate glitch matrix) glitch-rate)
     :drops (mapv #(update-drop % tick) drops)
     :time (now)
     :tick (- (now) time)}))

(defn hue [drop y]
  (let [{:keys [head size]} drop
        dist (- head y)
        fade (int (* green 2 (- 1 (/ dist size))))]
    (cond
      (< dist 1) "white"
      (< dist 3) "silver"
      (< dist (* 0.5 size)) (str "rgb(0," green ",0)")
      (< dist size) (str "rgb(0," fade ",0)"))))

(defn render-glyph [glyph fill x y]
  (aset ctx "fillStyle" fill)
  (.setTransform ctx -1 0 0 1 0 0)
  (.fillText ctx glyph (* font-size (- -0.5 x)) (* font-size (inc y)))
  (.setTransform ctx 1 0 0 1 0 0))

(defn render-drop [state x]
  (let [{:keys [matrix drops]} state
        drop (drops x)
        head (:head drop)
        tail (- head (:size drop))]
    (doseq [y (range h)]
      (when (>= head y tail)
        (render-glyph ((matrix x) y) (hue drop y) x y)))))

(defn render-drops [state]
  (doseq [x (range w)]
    (render-drop state x)))

(defn render-fps [state]
  (let [fps (.floor js/Math (/ 1000.0 (:tick state)))]
    (aset ctx "fillStyle" "white")
    (.fillText ctx (str "fps: " fps) 32 16)))

(defn render [state]
  (.clearRect ctx 0 0 (.-width cvs) (.-height cvs))
  (render-drops state)
  (render-fps state))

(defn setup []
  (aset ctx "font" (str font-size "px monospace"))
  (aset ctx "textAlign" "center")
  (reset! state (init-state)))

(defn rain []
  (render (swap! state update-state))
  (.requestAnimationFrame js/window rain))

(setup)
(.requestAnimationFrame js/window rain)
