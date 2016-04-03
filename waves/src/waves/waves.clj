(ns waves.waves
  (:require [quil.core :as q]
            [waves.vectors :as v]))

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
  (q/with-translation [(get-in wave [:origin :x]) (get-in wave [:origin :y])]
  	(q/with-rotation [(:rotation wave)]
	  (q/begin-shape)
        (doseq [pt (:points wave)]
          (q/vertex (:x pt) (:y pt)))
        (q/end-shape))))