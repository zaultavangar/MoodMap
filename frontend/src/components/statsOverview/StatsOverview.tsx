import { useRecoilState, useRecoilValue } from 'recoil';
import './StatsOverview.css';
import { isExpandedStatsOverviewState, selectedDateRangeState } from '~/atoms';
import { LocationToDetailsMap, useStatsOverview } from '~/hooks/useStatsOverview';
import { MapRef } from 'react-map-gl';
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess';
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore';

export const StatsOverview = ({
  mapRef
  }: 
  {
  mapRef: React.RefObject<MapRef>
}) => {
  const selectedDateRange = useRecoilValue(selectedDateRangeState);
  const [isExpandedStatsOverview, setIsExpandedStatsOverview] = useRecoilState(isExpandedStatsOverviewState);



  const { getStatsOverviewMap } = useStatsOverview();

  const mostMentioned = getStatsOverviewMap().mostMentioned;
  const mostPostive = getStatsOverviewMap().mostPositive;
  const mostNegative = getStatsOverviewMap().mostNegative;



  const getFullDateFromDateStr = () => {
    const year = parseInt(selectedDateRange.substring(3,7));
    const month = parseInt(selectedDateRange.substring(0,2)) - 1; 

    const date = new Date(year, month);
    return date.toLocaleString('default', { month: 'short', year: 'numeric' });
  }

  const handleLocationClick = (coordinates: number[]) => {
    console.error("Coords: ", coordinates)
    console.error(mapRef.current)
    if (mapRef.current){
      mapRef.current.flyTo({
        center: [coordinates[0], coordinates[1]],
        zoom: 12
      })
    }
  }

  const toggleStatsOverviewDisplay = () => {
    setIsExpandedStatsOverview(!isExpandedStatsOverview);
  }

  const renderList = (list: LocationToDetailsMap[], label: string, isCountList = false) => (
    <div className='top-five-extreme-container'>
      <div id='list-header'>
        {label}
      </div>
      <div className='top-five-list'>
        {list.map((m, idx) => {
          const key = Object.keys(m)[0];
          const locationName = key.length >= 16 ? `${key.substring(0,16)}...` : key;
          const value = isCountList ? m[key].count : m[key].sentiment.toFixed(2);
  
          return (
          <div key={idx} className='location-container' onClick={() => handleLocationClick(m[key].coordinates)}>
              <div id='location-name'>{locationName}</div>
              <div>{value}</div>
            </div>
          );
        })}
      </div>
    </div>
  );


  return (
    <div className='stats-overview-container'>
      {isExpandedStatsOverview ? 
        <>
         <div className='header-container'>
          <div id='stats-overview-title'><u>Overview for {getFullDateFromDateStr()}</u></div>
          <UnfoldLessIcon className='toggle-icon' onClick={toggleStatsOverviewDisplay}/>
        </div>
        <div className='most-extremes-container'>
          {renderList(mostMentioned, 'Most Mentioned', true)}
          {renderList(mostPostive, 'Most Positive')}
          {renderList(mostNegative.reverse(), 'Most Negative')}
        </div>
       </>
      :
        <div className='condensed-stats-overview' onClick={toggleStatsOverviewDisplay}>
          <div>See overview for {getFullDateFromDateStr()}</div>
          <UnfoldMoreIcon className='toggle-icon'/>
        </div>
      }
       
    </div>
  )
}
