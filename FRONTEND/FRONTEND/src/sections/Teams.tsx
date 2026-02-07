import { motion } from 'framer-motion';
import { 
  Trophy, 
  MapPin, 
  User, 
  Calendar, 
  MoreHorizontal, 
  Plus,
  Search,
  Filter,
  ArrowUpDown
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import type { Team } from '@/types';
import { mockTeams } from '@/services/api';
import { useState } from 'react';

interface TeamsProps {
  teams?: Team[];
}

export function Teams({ teams = mockTeams }: TeamsProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [sortField, setSortField] = useState<keyof Team>('name');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');

  const filteredTeams = teams.filter(team =>
    team.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    team.sport.toLowerCase().includes(searchQuery.toLowerCase()) ||
    team.coach.toLowerCase().includes(searchQuery.toLowerCase()) ||
    team.location.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const sortedTeams = [...filteredTeams].sort((a, b) => {
    const aValue = a[sortField];
    const bValue = b[sortField];
    if (aValue === undefined || bValue === undefined) return 0;
    
    const comparison = String(aValue).localeCompare(String(bValue));
    return sortDirection === 'asc' ? comparison : -comparison;
  });

  const handleSort = (field: keyof Team) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  const sportColors: Record<string, string> = {
    Football: 'from-blue-500 to-cyan-500',
    Basketball: 'from-orange-500 to-red-500',
    Baseball: 'from-green-500 to-emerald-500',
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
              placeholder="Search teams..."
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
          Add Team
        </Button>
      </div>

      {/* Teams Table */}
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
                    Team Name
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('sport')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Sport
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('coach')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Coach
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <button
                    onClick={() => handleSort('location')}
                    className="flex items-center gap-2 text-sm font-medium text-white/60 hover:text-white transition-colors"
                  >
                    Location
                    <ArrowUpDown className="w-3 h-3" />
                  </button>
                </th>
                <th className="text-left py-4 px-6">
                  <span className="text-sm font-medium text-white/60">Founded</span>
                </th>
                <th className="text-right py-4 px-6">
                  <span className="text-sm font-medium text-white/60">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {sortedTeams.map((team, index) => (
                <motion.tr
                  key={team.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.4, delay: 0.3 + index * 0.05 }}
                  className="border-b border-white/5 table-row-hover group"
                >
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-4">
                      <div className={`
                        w-10 h-10 rounded-xl bg-gradient-to-br ${sportColors[team.sport] || 'from-gray-500 to-gray-600'}
                        flex items-center justify-center shadow-lg
                      `}>
                        <Trophy className="w-5 h-5 text-white" />
                      </div>
                      <div>
                        <p className="font-medium text-white group-hover:text-neon-blue transition-colors">
                          {team.name}
                        </p>
                        <p className="text-sm text-white/40">ID: #{team.id}</p>
                      </div>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <span className={`
                      px-3 py-1 rounded-full text-xs font-medium
                      ${team.sport === 'Football' ? 'bg-blue-500/20 text-blue-400' :
                        team.sport === 'Basketball' ? 'bg-orange-500/20 text-orange-400' :
                        team.sport === 'Baseball' ? 'bg-green-500/20 text-green-400' :
                        'bg-white/10 text-white/60'}
                    `}>
                      {team.sport}
                    </span>
                  </td>
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2">
                      <User className="w-4 h-4 text-white/40" />
                      <span className="text-white/80">{team.coach}</span>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2">
                      <MapPin className="w-4 h-4 text-white/40" />
                      <span className="text-white/80">{team.location}</span>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2">
                      <Calendar className="w-4 h-4 text-white/40" />
                      <span className="text-white/80">{team.foundedYear || 'N/A'}</span>
                    </div>
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
              ))}
            </tbody>
          </table>
        </div>
        
        {/* Pagination */}
        <div className="flex items-center justify-between py-4 px-6 border-t border-white/5">
          <p className="text-sm text-white/40">
            Showing {sortedTeams.length} of {teams.length} teams
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

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {[
          { label: 'Total Teams', value: teams.length, color: 'blue' },
          { label: 'Football', value: teams.filter(t => t.sport === 'Football').length, color: 'green' },
          { label: 'Basketball', value: teams.filter(t => t.sport === 'Basketball').length, color: 'orange' },
          { label: 'Baseball', value: teams.filter(t => t.sport === 'Baseball').length, color: 'purple' },
        ].map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: 0.5 + index * 0.1 }}
            className="glass-card rounded-xl p-4"
          >
            <p className="text-sm text-white/50">{stat.label}</p>
            <p className={`text-2xl font-bold text-${stat.color}-400`}>{stat.value}</p>
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
}
