(ns basic-particles.particle
  (:require [quil.core :as q]
            [basic-particles.protocols.particle-operations :refer :all]
            [basic-particles.protocols.vector-operations :refer :all]))

(defrecord Particle [location velocity lifespan angle]
  
  ParticleOperations
  
  (update-particle [p [& forces]]
  	(let [new-lifespan (- lifespan 2)
          acceleration (reduce add forces)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)
          angular-velocity (/ (:x new-velocity) 10)
          new-angle (+ angle angular-velocity)]
     (Particle. new-location new-velocity new-lifespan new-angle)))
  
  (render-particle [p]
  	(q/stroke 0 lifespan)
    (q/rect-mode :center)
   	(q/fill 175 lifespan)
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [angle]
        (q/rect 0 0 8 8))))
  
  (is-alive? [p]
  	(> lifespan 0)))