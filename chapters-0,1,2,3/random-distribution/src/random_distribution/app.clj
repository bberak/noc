(ns random-distribution.app
  (:require [quil.core :as q]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 60)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; create seq of maps containing a num and count
  {:t 0
   :bars (vec (map (fn [n] {:count 0})
            (range 1 20)))})

(defn get-random [min max]
  (int (q/random min max)))

(defn get-gaussian-random [min max]
  (let [mean (/ (+ min max) 2)
        sd 5
        next-rand (q/random-gaussian)]
  (int (+ (* sd next-rand) mean))))

(defn get-monte-carlo-random [min max]
  (let [r1 (q/random min max)
        p r1
        r2 (q/random min max)]
    (if (< r2 p)
      (int r1)
      (get-monte-carlo-random min max))))

(defn get-noise-random [t max-num]
  (int (* (q/noise t) max-num)))

(defn update-state [state]
  ; Update the graph by selecting a new random number
  ; and updating the count on the corresponding key-value-pair
  (let [bars (:bars state)
        next-t (+ (:t state) 0.01)
        min-num 0
        max-num (count bars)
        next-random-num (get-gaussian-random min-num max-num)]
    {:t next-t
     :bars (update-in bars [next-random-num :count] inc)}))

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; set a fill color for the bars
  (q/fill 175)
  ; Calculate the width of each bar
  (let [bars (:bars state)
        num-bars (count bars)
        window-width (q/width)
        window-height (q/height)
        bar-width (/ window-width num-bars)]
    (doall (map-indexed (fn [idx num]
                   (let [x (* idx bar-width)
                         bar-height (:count num)
                         y (- window-height bar-height)]
                     (q/rect x y bar-width bar-height)))
                 bars))))
