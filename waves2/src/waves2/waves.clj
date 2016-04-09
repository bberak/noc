(ns waves2.waves
  (:require [quil.core :as q]
            [waves2.vectors :as v]))

(def two-pi (q/radians 360))

(defn wave [origin-vector width period amplitude speed num-points rotation function]
  {:origin origin-vector
   :period period
   :amplitude amplitude
   :speed speed
   :width width
   :num-points num-points
   :theta 0
   :dx (/ width num-points)
   :points []
   :rotation rotation
   :function function})

(defn sine-wave [origin-vector width period amplitude speed num-points rotation]
  (wave origin-vector width period amplitude speed num-points rotation (fn [amp theta period] (* amp (q/sin (* two-pi (/ theta period)))))))

(defn cosine-wave [origin-vector width period amplitude speed num-points rotation]
  (wave origin-vector width period amplitude speed num-points rotation (fn [amp theta period] (* amp (q/cos (* two-pi (/ theta period)))))))

(defn add [& waves]
  (reduce (fn [w1 w2]
            (let [new-width (+ (:width w1) (:width w2))
                  new-num-points (+ (:num-points w1) (:num-points w2))]
            {:origin (v/add (:origin w1) (:origin w2))
             :period (+ (:period w1) (:period w2))
             :amplitude (+ (:amplitude w1) (:amplitude w2))
             :speed (+ (:speed w1) (:speed w2))
             :width new-width
             :num-points new-num-points
             :theta (+ (:theta w1) (:theta w2))
             :dx (/ new-width new-num-points)
             :points []
             :rotation (+ (:rotation w1) (:rotation w2))
             :function (fn [amp theta period]
                         (let [f1 (:function w1)
                               f2 (:function w2)]
                           (+ (f1 amp theta period) (f2 amp theta period))))}))
          waves))

(defn update-wave [wave]
  (let [theta (:theta wave)
        amplitude (:amplitude wave)
        width (:width wave)
        period (:period wave)
        speed (:speed wave)
        dx (:dx wave)
        function (:function wave)]
    (assoc wave :points (map (fn [x]
                               (let [d-theta (+ x theta)
                                     y (function amplitude d-theta period)]
                                 (v/create x y)))
                             (range 0 width dx))
      			:theta (+ theta speed))))

(defn render [wave]
  (q/fill 255)
  (q/with-translation [(get-in wave [:origin :x]) (get-in wave [:origin :y])]
  	(q/with-rotation [(:rotation wave)]
  	  (q/begin-shape)
        (doseq [pt (:points wave)]
          (q/curve-vertex (:x pt) (:y pt)))
      (q/end-shape))))

(defn render-ellipses [wave width height r g b a]
  (q/fill r g b a)
  (q/with-translation [(get-in wave [:origin :x]) (get-in wave [:origin :y])]
    (q/with-rotation [(:rotation wave)]
      (doseq [pt (:points wave)]
        (q/ellipse (:x pt) (:y pt) width height)))))

(defn render-rects [wave width height r g b a]
  (q/fill r g b a)
  (q/rect-mode :center)
  (q/with-translation [(get-in wave [:origin :x]) (get-in wave [:origin :y])]
    (q/with-rotation [(:rotation wave)]
      (doseq [pt (:points wave)]
        (q/rect (:x pt) (:y pt) width height)))))

(defn render-rects-with-heading [wave width height r g b a]
  (q/fill r g b a)
  (q/rect-mode :center)
  (q/with-translation [(get-in wave [:origin :x]) (get-in wave [:origin :y])]
    (q/with-rotation [(:rotation wave)]
      (let [points (:points wave)]
        (if (not-empty points)
          (loop [[head & tail] points]
            (let [next-point (or (first tail) head)
                  direction (v/subtract next-point head)
                  heading (q/atan2 (:y direction) (:x direction))]
              (q/with-translation [(:x head) (:y head)]                   
                (q/with-rotation [heading]
                  (q/rect 0 0 width height))))
            (if (not-empty tail) 
              (recur tail))))))))




