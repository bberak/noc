(ns basic-particles.records.square-particle
  (:require [quil.core :as q]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.vector :refer :all]))

(defrecord SquareParticle [location velocity lifespan angle]
  
  Particle
  
  (update-particle [p [& forces]]
  	(let [new-lifespan (- lifespan 2)
          acceleration (reduce add (->Vector2D 0 0) forces)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)
          angular-velocity (/ (:x new-velocity) 10)
          new-angle (+ angle angular-velocity)]
     (SquareParticle. new-location new-velocity new-lifespan new-angle)))
  
  (render-particle [p]
  	(q/stroke 0 lifespan)
    (q/rect-mode :center)
   	(q/fill 175 lifespan)
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [angle]
        (q/rect 0 0 8 8))))
  
  (is-alive? [p]
  	(> lifespan 0)))