import { motion } from 'framer-motion';
import { 
  Star, 
  Calendar, 
  MoreHorizontal, 
  Plus,
  Search,
  Filter,
  ArrowUpDown,
  TrendingUp,
  TrendingDown,
  Minus
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import type { Player } from '@/types';
import { mockPlayers } from '@/services/api';
import { useState } from 'react';

interface PlayersProps {
  players?: Player[];
}

export function Players({ players = mockPlayers }: PlayersProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [sortField, setSortField] = useState<string>('rating');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  const filteredPlayers = players.filter(player =>
    `${player.firstName} ${player.lastName}`.toLowerCase().includes(searchQuery.toLowerCase()) ||
    player.position.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const sortedPlayers = [...filteredPlayers].sort((a, b) => {
    let aValue: number | string = '';
    let bValue: number | string = '';
    
    if (sortField === 'name') {
      aValue = `${a.firstName} ${a.lastName}`;
      bValue = `${b.firstName} ${b.lastName}`;
    } else if (sortField === 'rating') {
      aValue = a.rating;
      bValue = b.rating;
    } else if (sortField === 'age') {
      aValue = a.age;
      bValue = b.age;
    } else if (sortField === 'position') {
      aValue = a.position;
      bValue = b.position;
    }
    
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      const comparison = aValue.localeCompare(bValue);
      return sortDirection === 'asc' ? comparison : -comparison;
    }
    
    const comparison = (aValue as number) - (bValue as number);
    return sortDirection === 'asc' ? comparison : -comparison;
  });

  const handleSort = (field: string) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  const getRatingColor = (rating: number) => {
    if (rating >= 9.5) return 'text-neon-green';
    if (rating >= 9.0) return 'text-neon-blue';
    if (rating >= 8.0) return 'text-neon-purple';
    return 'text-white/60';
  };

  const getRatingBg = (rating: number) => {
    if (rating >= 9.5) return 'bg-neon-green/20';
    if (rating >= 9.0) return 'bg-neon-blue/20';
    if (rating >= 8.0) return 'bg-neon-purple/20';
    return 'bg-white/10';
  };

  const getTrend = (index: number) => {
    if (index < 3) return 'up';
    if (index > 6) return 'down';
    return 'neutral';
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
      className="space-y-6 p-8"
    >
      {/* Header Actions */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
            <Input
              type="text"
              placeholder="Search players..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-80 pl-10 bg-white/5 border-white/10 text-white placeholder:text-white/30 
                         focus:border-neon-blue/50 rounded-xl"
            />
          </div>
          <Button
            variant="outline"
            className="border-white/10 text-white/70 hover:bg-white/5 hover:text-white rounded-xl"
          >
            <Filter className="w-4 h-4 mr-2" />
            Filter
          </Button>
        </div>
        
        <Button className="bg-neon-blue hover:bg-neon-blue/90 text-white rounded-xl shadow-glow">
          <Plus className="w-4 h-4 mr-2" />
          Add Player
        </Button>
      </div>

      {/* Players Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="glass-card rounded-2xl overflow-hidden"
      >
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-white/5">
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('name')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Player
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('position')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Position
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('age')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Age
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('rating')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Rating
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <span className="text-sm font-medium text-white/60">Jersey #</span>
                </th>
                <th className="text-left py-4 px-6">
                  <span className="text-sm font-medium text-white/60">Status</span>
                </th>
                <th className="text-right py-4 px-6">
                  <span className="text-sm font-medium text-white/60">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {sortedPlayers.map((player, index) => {
                const trend = getTrend(index);
                const TrendIcon = trend === 'up' ? TrendingUp : trend === 'down' ? TrendingDown : Minus;
                const trendColor = trend === 'up' ? 'text-neon-green' : trend === 'down' ? 'text-red-400' : 'text-white/40';
                
                return (
                  <motion.tr
                    key={player.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.4, delay: 0.3 + index * 0.05 }}
                    className="border-b border-white/5 table-row-hover group"
                  >
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-4">
                        <div className={`
                          w-10 h-10 rounded-xl flex items-center justify-center text-white font-bold
                          ${getRatingBg(player.rating)}
                        `}>
                          {player.firstName[0]}{player.lastName[0]}
                        </div>
                        <div>
                          <p className="font-medium text-white group-hover:text-neon-blue transition-colors">
                            {player.firstName} {player.lastName}
                          </p>
                          <p className="text-sm text-white/40">ID: #{player.id}</p>
                        </div>
                      </div>
                    </td>
                    <td className="py-4 px-6">
                      <span className="px-3 py-1 rounded-full text-xs font-medium bg-white/10 text-white/70">
                        {player.position}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-2">
                        <Calendar className="w-4 h-4 text-white/40" />
                        <span className="text-white/80">{player.age} years</span>
                      </div>
                    </td>
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-2">
                        <div className={`
                          flex items-center gap-1 px-3 py-1 rounded-full
                          ${getRatingBg(player.rating)}
                        `}>
                          <Star className={`w-4 h-4 ${getRatingColor(player.rating)}`} />
                          <span className={`font-bold ${getRatingColor(player.rating)}`}>
                            {player.rating}
                          </span>
                        </div>
                        <TrendIcon className={`w-4 h-4 ${trendColor}`} />
                      </div>
                    </td>
                    <td className="py-4 px-6">
                      <span className="text-white/80 font-mono">
                        #{player.jerseyNumber || 'N/A'}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      {player.teamId ? (
                        <span className="flex items-center gap-2">
                          <span className="w-2 h-2 rounded-full bg-neon-green animate-pulse" />
                          <span className="text-sm text-neon-green">Active</span>
                        </span>
                      ) : (
                        <span className="flex items-center gap-2">
                          <span className="w-2 h-2 rounded-full bg-neon-orange" />
                          <span className="text-sm text-neon-orange">Free Agent</span>
                        </span>
                      )}
                    </td>
                    <td className="py-4 px-6 text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-white/40 hover:text-white hover:bg-white/5"
                      >
                        <MoreHorizontal className="w-4 h-4" />
                      </Button>
                    </td>
                  </motion.tr>
                );
              })}
            </tbody>
          </table>
        </div>
        
        {/* Pagination */}
        <div className="flex items-center justify-between py-4 px-6 border-t border-white/5">
          <p className="text-sm text-white/40">
            Showing {sortedPlayers.length} of {players.length} players
          </p>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              className="border-white/10 text-white/60 hover:bg-white/5 hover:text-white"
              disabled
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              className="border-white/10 text-white/60 hover:bg-white/5 hover:text-white"
              disabled
            >
              Next
            </Button>
          </div>
        </div>
      </motion.div>

      {/* Rating Distribution */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.4 }}
        className="glass-card rounded-2xl p-6"
      >
        <h3 className="text-lg font-semibold text-white mb-4">Rating Distribution</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {[
            { range: '9.5+', label: 'Elite', count: players.filter(p => p.rating >= 9.5).length, color: 'neon-green' },
            { range: '9.0-9.4', label: 'Excellent', count: players.filter(p => p.rating >= 9.0 && p.rating < 9.5).length, color: 'neon-blue' },
            { range: '8.5-8.9', label: 'Very Good', count: players.filter(p => p.rating >= 8.5 && p.rating < 9.0).length, color: 'neon-purple' },
            { range: '8.0-8.4', label: 'Good', count: players.filter(p => p.rating >= 8.0 && p.rating < 8.5).length, color: 'neon-orange' },
          ].map((item, index) => (
            <motion.div
              key={item.range}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.4, delay: 0.5 + index * 0.1 }}
              className="p-4 rounded-xl bg-white/[0.02] border border-white/5"
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-white/60">{item.range}</span>
                <span className={`text-${item.color} font-bold`}>{item.count}</span>
              </div>
              <p className="text-sm text-white/40">{item.label}</p>
              <div className="mt-3 h-1 bg-white/10 rounded-full overflow-hidden">
                <motion.div
                  initial={{ width: 0 }}
                  animate={{ width: `${(item.count / players.length) * 100}%` }}
                  transition={{ duration: 1, delay: 0.7 + index * 0.1 }}
                  className={`h-full bg-${item.color} rounded-full`}
                />
              </div>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </motion.div>
  );
}
