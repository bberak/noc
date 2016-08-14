(ns clouds.puff
  (:require [quil.core :as q]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.vector :refer :all]))

(defrecord Puff [location velocity lifespan variance]
  
  Particle
  
  (step [p [& forces]]
  	(let [new-lifespan (- lifespan 1)
          acceleration (reduce add (->Vector2D 0 0) forces)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)]
     (Puff. new-location new-velocity new-lifespan variance)))
  
  (render [p]
    (q/no-stroke)
    (q/fill 255 (q/map-range lifespan 255 0 125 0))	
    (q/with-translation [(:x location) (:y location)]
	  (let [radius (* 40 (q/norm lifespan 0 255))]
      	(q/ellipse 0 0 (+ variance radius) (+ variance radius)))))
  
  (is-alive? [p]
  	(> lifespan 0)))