(ns rocket.smoke
  (:require [quil.core :as q]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.vector :refer :all]))

(defrecord Smoke [location velocity lifespan angle]
  
  Particle
  
  (update-particle [p [& forces]]
  	(let [new-lifespan (- lifespan 2)
          acceleration (reduce add (->Vector2D 0 0) forces)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)
          angular-velocity (/ (:x new-velocity) 10)
          new-angle (+ angle angular-velocity)]
     (Smoke. new-location new-velocity new-lifespan new-angle)))
  
  (render-particle [p]
  	(q/stroke 3)
   	(q/fill 175 lifespan)
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [angle]
        (let [radius (* 80 (/ 1 lifespan))]
          (q/ellipse 0 0 radius radius)))))
  
  (is-alive? [p]
  	(> lifespan 0)))