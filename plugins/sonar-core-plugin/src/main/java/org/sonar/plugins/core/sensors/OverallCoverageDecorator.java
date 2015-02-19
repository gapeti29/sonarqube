/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.core.sensors;

import com.google.common.collect.ImmutableList;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;

import java.util.Collection;

public final class OverallCoverageDecorator extends AbstractCoverageDecorator {

  public OverallCoverageDecorator(Settings settings) {
    super(settings);
  }

  @DependsUpon
  public Collection<Metric> usedMetrics() {
    return ImmutableList.<Metric>of(CoreMetrics.OVERALL_LINES_TO_COVER, CoreMetrics.OVERALL_UNCOVERED_LINES, CoreMetrics.NEW_OVERALL_LINES_TO_COVER,
      CoreMetrics.NEW_OVERALL_UNCOVERED_LINES, CoreMetrics.OVERALL_CONDITIONS_TO_COVER, CoreMetrics.OVERALL_UNCOVERED_CONDITIONS,
      CoreMetrics.NEW_OVERALL_CONDITIONS_TO_COVER, CoreMetrics.NEW_OVERALL_UNCOVERED_CONDITIONS);
  }

  @Override
  protected Metric getGeneratedMetric() {
    return CoreMetrics.OVERALL_COVERAGE;
  }

  @Override
  protected Long countElements(DecoratorContext context) {
    long lines = MeasureUtils.getValueAsLong(context.getMeasure(CoreMetrics.OVERALL_LINES_TO_COVER), 0L);
    long conditions = MeasureUtils.getValueAsLong(context.getMeasure(CoreMetrics.OVERALL_CONDITIONS_TO_COVER), 0L);

    return lines + conditions;
  }

  @Override
  protected long countCoveredElements(DecoratorContext context) {
    long uncoveredLines = MeasureUtils.getValueAsLong(context.getMeasure(CoreMetrics.OVERALL_UNCOVERED_LINES), 0L);
    long lines = MeasureUtils.getValueAsLong(context.getMeasure(CoreMetrics.OVERALL_LINES_TO_COVER), 0L);
    long uncoveredConditions = MeasureUtils.getValueAsLong(context.getMeasure(CoreMetrics.OVERALL_UNCOVERED_CONDITIONS), 0L);
    long conditions = MeasureUtils.getValueAsLong(context.getMeasure(CoreMetrics.OVERALL_CONDITIONS_TO_COVER), 0L);

    return lines + conditions - uncoveredConditions - uncoveredLines;
  }

  @Override
  protected Metric getGeneratedMetricForNewCode() {
    return CoreMetrics.NEW_OVERALL_COVERAGE;
  }

  @Override
  protected Long countElementsForNewCode(DecoratorContext context, int periodIndex) {
    Long newLinesToCover = MeasureUtils.getVariationAsLong(context.getMeasure(CoreMetrics.NEW_OVERALL_LINES_TO_COVER), periodIndex);
    if (newLinesToCover == null) {
      return null;
    }

    long newConditionsToCover = MeasureUtils.getVariationAsLong(context.getMeasure(CoreMetrics.NEW_OVERALL_CONDITIONS_TO_COVER), periodIndex, 0L);

    return newLinesToCover + newConditionsToCover;
  }

  @Override
  protected long countCoveredElementsForNewCode(DecoratorContext context, int periodIndex) {
    long newLines = MeasureUtils.getVariationAsLong(context.getMeasure(CoreMetrics.NEW_OVERALL_LINES_TO_COVER), periodIndex, 0L);
    long newUncoveredLines = MeasureUtils.getVariationAsLong(context.getMeasure(CoreMetrics.NEW_OVERALL_UNCOVERED_LINES), periodIndex, 0L);
    long newUncoveredConditions = MeasureUtils.getVariationAsLong(context.getMeasure(CoreMetrics.NEW_OVERALL_UNCOVERED_CONDITIONS), periodIndex, 0L);
    long newConditions = MeasureUtils.getVariationAsLong(context.getMeasure(CoreMetrics.NEW_OVERALL_CONDITIONS_TO_COVER), periodIndex, 0L);

    return newLines + newConditions - newUncoveredConditions - newUncoveredLines;
  }
}
