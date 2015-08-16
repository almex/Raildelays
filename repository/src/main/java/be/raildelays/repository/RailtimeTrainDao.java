/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.repository;

import be.raildelays.domain.entities.RailtimeTrain;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository that manage storing a {@link RailtimeTrain}.
 *
 * @author Almex
 */
public interface RailtimeTrainDao extends JpaRepository<RailtimeTrain, String> {

    /**
     * Search for a RailtimeTrain by its English name.
     *
     * @param name     strict name that should match to find a RailtimeTrain.
     * @param language in which you want to do the search.
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByEnglishName(String englishName);


    /**
     * Search for a RailtimeTrain by its French name.
     *
     * @param name     strict name that should match to find a RailtimeTrain.
     * @param language in which you want to do the search.
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByFrenchName(String frenchName);


    /**
     * Search for a RailtimeTrain by its Dutch name.
     *
     * @param name     strict name that should match to find a RailtimeTrain.
     * @param language in which you want to do the search.
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByDutchName(String dutchName);


    /**
     * Search for a RailtimeTrain by its Id coming from Railtime.
     *
     * @param railtimeId Id coming from Railtime
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByRailtimeId(String railtimeId);
}
