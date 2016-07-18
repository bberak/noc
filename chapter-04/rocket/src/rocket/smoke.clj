(ns rocket.smoke
  (:require [quil.core :as q]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.vector :refer :all]))

(defrecord Smoke [location velocity lifespan angle variance]
  
  Particle
  
  (step [p [& forces]]
  	(let [new-lifespan (- lifespan 2)
          acceleration (reduce add (->Vector2D 0 0) forces)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)
          angular-velocity (/ (:x new-velocity) 10)
          new-angle (+ angle angular-velocity)]
     (Smoke. new-location new-velocity new-lifespan new-angle variance)))
  
  (render [p]
    (q/fill 175 lifespan)	
    (q/stroke 3 lifespan)
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [angle]
        (let [radius (* 40 (q/norm lifespan 75 0))]
          (q/ellipse 0 0 (+ variance radius) (+ variance radius))))))
  
  (is-alive? [p]
  	(> lifespan 0)))